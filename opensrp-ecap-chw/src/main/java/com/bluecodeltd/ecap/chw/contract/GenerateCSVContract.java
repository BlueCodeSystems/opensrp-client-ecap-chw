package com.bluecodeltd.ecap.chw.contract;

public interface GenerateCSVContract {
    interface View {
        void showCSVGeneratedMessage(String filePath);
        void showError(String errorMessage);
    }

    interface Presenter {
        void generateCSV();
    }
}

