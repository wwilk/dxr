package pl.devoxx.dxr.android.activity.settings;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import pl.devoxx.dxr.android.appearanceType.AppearanceType;

public class AppearanceTypeDropdown implements OnItemClickListener {

    private final Context context;
    private final PopupWindow popupWindow;
    private final SelectCallback selectCallback;
    private final Button buttonShowDropDown;
    private final List<AppearanceType> allTypes;

    private AppearanceType selected;

    public AppearanceTypeDropdown(Context context, SelectCallback selectCallback, Button buttonShowDropDown, AppearanceType currentlySelected, List<AppearanceType> allTypes) {
        this.context = context;
        this.selectCallback = selectCallback;
        this.buttonShowDropDown = buttonShowDropDown;
        this.allTypes = allTypes;
        this.popupWindow = initPopupWindow();
        initShowDropdownButton(currentlySelected);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
        fadeInAnimation.setDuration(10);
        v.startAnimation(fadeInAnimation);

        popupWindow.dismiss();

        AppearanceType newSelected = (AppearanceType) v.getTag();
        setSelected(newSelected);

        selectCallback.onSelect();
    }

    public AppearanceType getSelected() {
        return selected;
    }

    private void setSelected(AppearanceType selected) {
        this.selected = selected;
        buttonShowDropDown.setText(selected.getName());
    }

    private void initShowDropdownButton(AppearanceType currentlySelectedAppearanceType){
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId() == buttonShowDropDown.getId()){
                    popupWindow.showAsDropDown(v, -5, 0);
                }
            }
        };
        buttonShowDropDown.setOnClickListener(handler);
        buttonShowDropDown.setText(currentlySelectedAppearanceType.getName());
    }

    private PopupWindow initPopupWindow() {
        PopupWindow popupWindow = new PopupWindow(context);
        ListView listViewDogs = new ListView(context);
        listViewDogs.setAdapter(appearanceTypeAdapter());
        listViewDogs.setOnItemClickListener(this);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.setContentView(listViewDogs);

        return popupWindow;
    }

    private ArrayAdapter<AppearanceType> appearanceTypeAdapter() {
        return new ArrayAdapter<AppearanceType>(context, android.R.layout.simple_list_item_1, allTypes) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                AppearanceType item = getItem(position);
                TextView listItem = new TextView(context);

                listItem.setText(item.getName());
                listItem.setTag(item);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.WHITE);

                return listItem;
            }
        };
    }

    public interface SelectCallback{
        void onSelect();
    }
}