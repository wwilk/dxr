package pl.devoxx.dxr.android.report;

import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.core.Template;
import pl.devoxx.dxr.api.results.Result;
import pl.devoxx.dxr.api.sponsor.SponsorRemoteService;

/**
 * Created by wilk on 09/08/15.
 */
public class SponsorReportTemplate extends Template<AppearanceType, Result>{

    private final SponsorRemoteService sponsorRemoteService;

    public SponsorReportTemplate(SponsorRemoteService sponsorRemoteService){
        this.sponsorRemoteService = sponsorRemoteService;
    }

    @Override
    protected Result callLocalDatabase(AppearanceType argument) {
        return new Result(false, "Please check your internet connection");
    }

    @Override
    protected Result callServer(AppearanceType argument) {
        return sponsorRemoteService.sendReport(argument.getName());
    }
}
