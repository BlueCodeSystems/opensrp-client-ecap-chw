package com.bluecodeltd.ecap.chw.presenter;

import com.bluecodeltd.ecap.chw.contract.GenerateCSVContract;
import com.bluecodeltd.ecap.chw.model.GenerateCSVsModel;

public class GenerateCSVPresenter implements GenerateCSVContract.Presenter {
    private final GenerateCSVContract.View view;
    private final GenerateCSVsModel generateCSVs;

    public GenerateCSVPresenter(GenerateCSVContract.View view) {
        this.view = view;
        this.generateCSVs = new GenerateCSVsModel();
    }

    @Override
    public void generateCSV() {
        generateCSVs.createCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createVcaCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createVcaServicesCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createHouseholdServicesCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createVCAVisitationsCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createHouseholdsVisitationsCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createReferralsCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createCaregiverHivAssessmentCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createVcaCasePlansCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createHouseholdCasePlansCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });
        generateCSVs.createVcaHivAssessmentCSVFile(new GenerateCSVsModel.CSVCallback() {
            @Override
            public void onSuccess(String filePath) {
                view.showCSVGeneratedMessage(filePath);
            }

            @Override
            public void onError(String error) {
                view.showError(error);
            }
        });


    }
}
