package edu.hm.cs.fs.app.datastore.helper;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.hm.cs.fs.app.datastore.model.Job;
import edu.hm.cs.fs.app.datastore.model.constants.Study;
import edu.hm.cs.fs.app.datastore.model.impl.GroupImpl;
import edu.hm.cs.fs.app.datastore.model.impl.JobImpl;
import edu.hm.cs.fs.app.datastore.web.JobFetcher;
import edu.hm.cs.fs.app.util.PrefUtils;
import io.realm.Realm;

public class JobHelper extends BaseHelper implements Job {
    private String title;
    private String contact;
    private String provider;
    private String description;
    private Study program;
    private Date expire;
    private String url;

    JobHelper(Context context, JobImpl job) {
        super(context);
        title = job.getTitle();
        contact = job.getContact();
        provider = job.getProvider();
        description = job.getDescription();
        program = GroupImpl.of(job.getProgram()).getStudy();
        expire = job.getExpire();
        url = job.getUrl();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Study getProgram() {
        return program;
    }

    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public Date getExpire() {
        return expire;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public static void listAll(final Context context, final Callback<List<Job>> callback) {
        PrefUtils.setUpdateInterval(context, JobFetcher.class, TimeUnit.MILLISECONDS.convert(1l, TimeUnit.DAYS));

        listAll(context, new JobFetcher(context), JobImpl.class, callback, new OnHelperCallback<Job, JobImpl>(JobImpl.class) {
            @Override
            public Job createHelper(Context context, JobImpl impl) {
                return new JobHelper(context, impl);
            }

            @Override
            public void copyToRealmOrUpdate(final Realm realm, final JobImpl job) {
                realm.copyToRealmOrUpdate(job);
            }
        });
    }

    static Job findById(final Context context, final String id) {
        return new RealmExecutor<Job>(context) {
            @Override
            public Job run(final Realm realm) {
                JobImpl job = realm.where(JobImpl.class).equalTo("id", id).findFirst();
                if (job == null) {
                    List<JobImpl> jobList = fetchOnlineData(new JobFetcher(context), realm, false, new OnHelperCallback<Job, JobImpl>() {
                        @Override
                        public Job createHelper(Context context, JobImpl impl) {
                            return new JobHelper(context, impl);
                        }

                        @Override
                        public void copyToRealmOrUpdate(final Realm realm, final JobImpl job) {
                            realm.copyToRealmOrUpdate(job);
                        }
                    });
                    for (JobImpl jobImpl : jobList) {
                        if (jobImpl.getId().equals(id)) {
                            job = jobImpl;
                            break;
                        }
                    }
                }
                return new JobHelper(context, job);
            }
        }.execute();
    }
}
