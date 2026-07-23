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
        generateCSVs.createAllEcClientFieldTablesCSVFiles(new GenerateCSVsModel.CSVCallback() {
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
