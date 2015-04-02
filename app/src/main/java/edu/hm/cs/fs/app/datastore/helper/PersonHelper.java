package edu.hm.cs.fs.app.datastore.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.hm.cs.fs.app.datastore.model.Person;
import edu.hm.cs.fs.app.datastore.model.constants.Day;
import edu.hm.cs.fs.app.datastore.model.constants.Faculty;
import edu.hm.cs.fs.app.datastore.model.constants.PersonStatus;
import edu.hm.cs.fs.app.datastore.model.constants.Sex;
import edu.hm.cs.fs.app.datastore.model.impl.PersonImpl;
import edu.hm.cs.fs.app.datastore.web.PersonFetcher;
import edu.hm.cs.fs.app.util.PrefUtils;
import io.realm.Realm;

/**
 * Created by Fabio on 03.03.2015.
 */
public class PersonHelper extends BaseHelper implements Person {
    private static final String TAG = PersonHelper.class.getSimpleName();
    private String lastName;
    private String firstName;
    private Sex sex;
    private String title;
    private Faculty faculty;
    private PersonStatus status;
    private boolean hidden;
    private String email;
    private String website;
    private String phone;
    private String function;
    private String focus;
    private String publication;
    private String office;
    private boolean emailOptin;
    private boolean referenceOptin;
    private Day officeHourWeekday;
    private String officeHourTime;
    private String officeHourRoom;
    private String officeHourComment;
    private String einsichtDate;
    private String einsichtTime;
    private String einsichtRoom;
    private String einsichtComment;

    PersonHelper(Context context, PersonImpl person) {
        super(context);
        lastName = person.getLastName();
        firstName = person.getFirstName();
        sex = Sex.of(person.getSex());
        title = person.getTitle();
        faculty = Faculty.of(person.getFaculty());
        status = PersonStatus.of(person.getStatus());
        hidden = person.isHidden();
        email = person.getEmail();
        website = person.getWebsite();
        phone = person.getPhone();
        function = person.getFunction();
        focus = person.getFocus();
        publication = person.getPublication();
        office = person.getOffice();
        emailOptin = person.isEmailOptin();
        referenceOptin = person.isReferenceOptin();
        officeHourWeekday = TextUtils.isEmpty(person.getOfficeHourWeekday()) ? null : Day.of(person.getOfficeHourWeekday());
        officeHourTime = person.getOfficeHourTime();
        officeHourRoom = person.getOfficeHourRoom();
        officeHourComment = person.getOfficeHourComment();
        einsichtDate = person.getEinsichtDate();
        einsichtTime = person.getEinsichtTime();
        einsichtRoom = person.getEinsichtRoom();
        einsichtComment = person.getEinsichtComment();
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public Sex getSex() {
        return sex;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Faculty getFaculty() {
        return faculty;
    }

    @Override
    public PersonStatus getStatus() {
        return status;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getWebsite() {
        return website;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getFunction() {
        return function;
    }

    @Override
    public String getFocus() {
        return focus;
    }

    @Override
    public String getPublication() {
        return publication;
    }

    @Override
    public String getOffice() {
        return office;
    }

    @Override
    public boolean isEmailOptin() {
        return emailOptin;
    }

    @Override
    public boolean isReferenceOptin() {
        return referenceOptin;
    }

    @Override
    public Day getOfficeHourWeekday() {
        return officeHourWeekday;
    }

    @Override
    public String getOfficeHourTime() {
        return officeHourTime;
    }

    @Override
    public String getOfficeHourRoom() {
        return officeHourRoom;
    }

    @Override
    public String getOfficeHourComment() {
        return officeHourComment;
    }

    @Override
    public String getEinsichtDate() {
        return einsichtDate;
    }

    @Override
    public String getEinsichtTime() {
        return einsichtTime;
    }

    @Override
    public String getEinsichtRoom() {
        return einsichtRoom;
    }

    @Override
    public String getEinsichtComment() {
        return einsichtComment;
    }

    public static void listAll(final Context context, final Callback<List<Person>> callback) {
        PrefUtils.setUpdateInterval(context, PersonFetcher.class, TimeUnit.MILLISECONDS.convert(50l, TimeUnit.DAYS));

        listAll(context, new PersonFetcher(context), PersonImpl.class, callback, new OnHelperCallback<Person, PersonImpl>() {
            @Override
            public Person createHelper(Context context, PersonImpl impl) {
                return new PersonHelper(context, impl);
            }

            @Override
            public void copyToRealmOrUpdate(Realm realm, PersonImpl impl) {
                realm.copyToRealmOrUpdate(impl);
            }
        });
    }

    static Person findById(final Context context, final String id) {
        return new RealmExecutor<Person>(context) {
            @Override
            public Person run(final Realm realm) {
                Log.d(TAG, "Search for " + id);
                PersonImpl person = realm.where(PersonImpl.class).equalTo("id", id).findFirst();
                if (person == null) {
                    Log.d(TAG, id + " not found -> search in web");
                    List<PersonImpl> personList = fetchOnlineData(new PersonFetcher(context, id), realm, false, new OnHelperCallback<Person, PersonImpl>() {
                        @Override
                        public Person createHelper(Context context, PersonImpl impl) {
                            return new PersonHelper(context, impl);
                        }

                        @Override
                        public void copyToRealmOrUpdate(Realm realm, PersonImpl impl) {
                            realm.copyToRealmOrUpdate(impl);
                        }
                    });
                    if(!personList.isEmpty()) {
                        person = personList.get(0);
                        Log.d(TAG, "Person: " + person.getId());
                    } else {
                        Log.d(TAG, "Person not found");
                    }
                } else {
                    Log.d(TAG, "Person: " + person.getId());
                }
                return new PersonHelper(context, person);
            }
        }.execute();
    }
}
