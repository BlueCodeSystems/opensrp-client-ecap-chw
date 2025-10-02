package org.smartregister.chw.anc.presenter;

import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitFragmentContract;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class BaseAncHomeVisitFragmentPresenter implements BaseAncHomeVisitFragmentContract.Presenter {

    private BaseAncHomeVisitFragmentContract.Model model;
    private WeakReference<BaseAncHomeVisitFragmentContract.View> view;

    public BaseAncHomeVisitFragmentPresenter(BaseAncHomeVisitFragmentContract.View view, BaseAncHomeVisitFragmentContract.Model model) {
        this.model = model;
        this.view = new WeakReference<>(view);

        initialize();
    }

    @Override
    public void initialize() {
        if (view.get() != null)
            model.processJson(view.get().getJsonObject(), this);
    }

    @Override
    public void setTitle(String title) {
        if (view.get() != null)
            view.get().setTitle(title);
    }

    @Override
    public void setQuestion(String question) {
        if (view.get() != null)
            view.get().setQuestion(question);
    }

    @Override
    public void setImageRes(int imageRes) {
        if (view.get() != null)
            view.get().setImageRes(imageRes);
    }

    @Override
    public void setQuestionType(BaseAncHomeVisitFragment.QuestionType questionType) {
        if (view.get() != null)
            view.get().setQuestionType(questionType);
    }

    @Override
    public void setInfoIconTitle(String infoIconTitle) {
        if (view.get() != null)
            view.get().setInfoIconTitle(infoIconTitle);
    }

    @Override
    public void setInfoIconDetails(String infoIconDetails) {
        if (view.get() != null) {
            view.get().setInfoIconDetails(infoIconDetails);
        }
    }

    @Override
    public BaseAncHomeVisitFragmentContract.View getView() {
        if (view.get() != null)
            return view.get();

        return null;
    }

    @Override
    public void writeValue(JSONObject jsonObject, String value) {
        model.writeValue(jsonObject, value);
    }

    @Override
    public void setValue(String value) {
        if (view.get() != null)
            view.get().setValue(value);
    }

    @Override
    public void setOptions(List<JSONObject> options) {
        if (view.get() != null)
            view.get().setOptions(options);
    }

    @Override
    public void setDateConstraints(Map<String, String> constraints) {
        if (view.get() != null) {
            view.get().setDateConstraints(constraints);
        }
    }


}
