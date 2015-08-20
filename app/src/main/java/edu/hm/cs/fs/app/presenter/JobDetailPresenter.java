package edu.hm.cs.fs.app.presenter;

import android.support.annotation.NonNull;

import edu.hm.cs.fs.app.database.ICallback;
import edu.hm.cs.fs.app.database.error.IError;
import edu.hm.cs.fs.app.database.model.JobModel;
import edu.hm.cs.fs.app.database.model.ModelFactory;
import edu.hm.cs.fs.app.util.MarkdownUtil;
import edu.hm.cs.fs.app.view.IJobDetailView;
import edu.hm.cs.fs.common.model.Job;

/**
 * Created by FHellman on 10.08.2015.
 */
public class JobDetailPresenter extends BasePresenter<IJobDetailView, JobModel> {
    /**
     * @param view
     */
    public JobDetailPresenter(IJobDetailView view) {
        this(view, ModelFactory.getJob());
    }

    /**
     * Needed for testing!
     *
     * @param view
     * @param model
     */
    public JobDetailPresenter(IJobDetailView view, JobModel model) {
        super(view, model);
    }

    public void loadJob(@NonNull final String title) {
        getView().showLoading();
        getModel().getItem(title, new ICallback<Job>() {
            @Override
            public void onSuccess(@NonNull Job data) {
                getView().showSubject(MarkdownUtil.toHtml(data.getTitle()));
                getView().showProvider(data.getProvider());
                getView().showDescription(MarkdownUtil.toHtml(data.getDescription()));
                getView().showUrl(data.getUrl());
                getView().showAuthor(data.getContact().getTitle() + " " +
                        data.getContact().getLastName() + " " +
                        data.getContact().getFirstName());
                getView().hideLoading();
            }

            @Override
            public void onError(@NonNull IError error) {
                getView().showError(error);
                getView().hideLoading();
            }
        });
    }
}
