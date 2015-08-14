package edu.hm.cs.fs.app.presenter;

import edu.hm.cs.fs.app.database.model.IModel;
import edu.hm.cs.fs.app.view.IView;

/**
 * Created by Fabio on 12.07.2015.
 */
public abstract class BasePresenter<V extends IView, M extends IModel> implements IPresenter {
    private final V view;
    private final M model;

    /**
     *
     * @param view
     * @param model
     */
    public BasePresenter(V view, M model) {
        this.view = view;
        this.model = model;
    }

    /**
     *
     * @return
     */
    protected M getModel() {
        return model;
    }

    /**
     *
     * @return
     */
    protected V getView() {
        return view;
    }
}
