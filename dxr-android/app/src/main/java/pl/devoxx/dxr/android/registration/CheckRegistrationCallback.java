package pl.devoxx.dxr.android.registration;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.user_info.UserInfoScreenAdapter;
import pl.devoxx.dxr.android.appearance.InsertAppearanceTaskFactory;
import pl.devoxx.dxr.android.core.Callback;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.Status;

/**
 * Created by wilk on 20/03/15.
 */
public class CheckRegistrationCallback implements Callback<PersonDto> {

    private final UserInfoScreenAdapter userInfoScreenAdapter;
    private final InsertAppearanceTaskFactory appearanceTaskFactory;

    public CheckRegistrationCallback(UserInfoScreenAdapter userInfoScreenAdapter, InsertAppearanceTaskFactory appearanceTaskFactory){
        this.userInfoScreenAdapter = userInfoScreenAdapter;
        this.appearanceTaskFactory = appearanceTaskFactory;
    }

    @Override
    public void onSuccess(PersonDto result) {
        if(result == null){
            userNotFound();
            return;
        }
        displayUserDetails(result);

        if(!hasPaid(result)){
            userRegisteredButNotPaid();
        } else if(!result.getAppearances().isEmpty()){
            userPaidButAlreadyAppeared();
        } else{
            userRegisteredAndPaid();
        }

        userInfoScreenAdapter.refreshAppearances(result.getAppearances());

        appearanceTaskFactory.create(result.getUserId()).execute();
    }

    private void userRegisteredButNotPaid() {
        userInfoScreenAdapter.playFailureSound();
        userInfoScreenAdapter.fillYellow();
        userInfoScreenAdapter.sendToast(userInfoScreenAdapter.getMessage(R.string.registration_user_didnt_pay));
    }

    private void userPaidButAlreadyAppeared() {
        userInfoScreenAdapter.fillRed();
        userInfoScreenAdapter.playFailureSound();
        userInfoScreenAdapter.sendToast(userInfoScreenAdapter.getMessage(R.string.registration_user_already_appeared));
    }

    private void userRegisteredAndPaid() {
        userInfoScreenAdapter.playSuccessSound();
        userInfoScreenAdapter.fillGreen();
        userInfoScreenAdapter.sendToast(userInfoScreenAdapter.getMessage(R.string.registration_user_paid_and_first_appearance));
    }

    private void displayUserDetails(PersonDto result) {
        userInfoScreenAdapter.displayName(result.getFirstName());
        userInfoScreenAdapter.displaySurname(result.getLastName());
        userInfoScreenAdapter.displayEmail(result.getEmail());
    }

    private void userNotFound() {
        userInfoScreenAdapter.fillRed();
        userInfoScreenAdapter.playFailureSound();
        userInfoScreenAdapter.sendToast(userInfoScreenAdapter.getMessage(R.string.registration_user_not_found));
        userInfoScreenAdapter.hideWriteButton();
    }

    private boolean hasPaid(PersonDto person){
        return person.getRegistrationStatus().isOneOf(Status.Paid, Status.FuturePayment);
    }

    @Override
    public void onError(Throwable serverError, Throwable localError) {
        userInfoScreenAdapter.sendToast(serverError.getMessage());
        userInfoScreenAdapter.sendToast(localError.getMessage());
        userInfoScreenAdapter.playFailureSound();
        userInfoScreenAdapter.fillRed();
    }
}
