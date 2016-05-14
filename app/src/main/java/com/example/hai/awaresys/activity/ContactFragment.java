package com.example.hai.awaresys.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hai.awaresys.R;
import com.example.hai.awaresys.dao.ContactDao;
import com.example.hai.awaresys.model.Contact;

/**
 * Created by Hai on 3/24/2016.
 */
public class ContactFragment extends Fragment {
    public static final String EXTRA_CONTACT_ID = "contactIntent.CONTACT_ID";
    public static final String EXTRA_EMPTY = "contactIntent.CONTACT_EMPTY";
    public static final int REQUEST_CONTACT = 1;
    private Contact mContact;
    private View v;
    private EditText mName, mNumber, mEmail;
    private Button chooseContact;
    private ContactDao contactDao;

    public static final String TAG = "ContactFragment";

    public static ContactFragment newInstance(String contactId) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CONTACT_ID, contactId);
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String contactId = getArguments().getString(EXTRA_CONTACT_ID);
        mContact = ContactLab.get(getContext()).getContact(contactId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_contact, parent, false);
        contactDao = new ContactDao(getActivity().getApplicationContext());
        mName = (EditText) v.findViewById(R.id.contact_name);
        mName.setText(mContact.getName());
        mName.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mContact.setName(c.toString());
                contactDao.update(mContact);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
            }
        });

        mName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    setResult();
                    Log.d(TAG, "name key ");
                }
                return false;
            }
        });

        mNumber = (EditText) v.findViewById(R.id.contact_number);
        mNumber.setText(mContact.getNumber());
        mNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                super.onTextChanged(c, start, before, count);
                mContact.setNumber(c.toString());
                contactDao.update(mContact);
                Log.d(TAG, mContact.getNumber() + " content");
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                super.beforeTextChanged(c, start, count, after);
            }

            public void afterTextChanged(Editable c) {
                super.afterTextChanged(c);
            }
        });

        mNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    setResult();
                    Log.d(TAG, "number key ");
                }
                return false;
            }
        });

        mEmail = (EditText) v.findViewById(R.id.contact_email);
        mEmail.setText(mContact.getEmail());
        mEmail.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mContact.setEmail(c.toString());
                contactDao.update(mContact);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            public void afterTextChanged(Editable c) {
            }
        });

        mEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    setResult();
                    if (!isValidEmail(mEmail.getText())) {
                        toastMessage(R.drawable.warn,"Invalid Email Address");
                    }
                    Log.d(TAG, "email key ");
                }
                return false;
            }
        });

        chooseContact = (Button) v.findViewById(R.id.choose_contact_button);
        chooseContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        return v;
    }

    public void toastMessage(int imageResource, String message){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) getView().findViewById(R.id.toast_layout_root));
        ImageView iv = (ImageView) layout.findViewById(R.id.toastImage);
        iv.setImageResource(imageResource);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT) {
            if (data != null) {
                setDetail(data.getData());
                mName.setText(mContact.getName());
                mNumber.setText(mContact.getNumber());
                mEmail.setText(mContact.getEmail());
                contactDao.update(mContact);
            }
        }
    }

    public void setDetail(Uri contactUri) {
        Cursor cursor = getActivity().getContentResolver()
                .query(contactUri, null, null, null, null);
        Log.d(TAG, "SetDetail");
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
        }

        cursor.moveToFirst();
        String contactId = cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts._ID));
        String name = cursor
                .getString(cursor
                        .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
        Log.d(TAG, name);
        String phoneNumber = "";
        String hasPhone = cursor
                .getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        boolean hasNumber = hasPhone.equalsIgnoreCase("1") ? true : false;
        if (hasNumber) {
            Cursor phones = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = " + contactId, null, null);
            phones.moveToNext();
            phoneNumber = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phones.close();
        }
        Log.d(TAG, phoneNumber);
        String emailAddress = "";
        Cursor emails = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
                        + contactId, null, null);
        if (emails.moveToNext()) {
            emailAddress = emails
                    .getString(emails
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        emails.close();
        Log.d(TAG, emailAddress);
        mContact.setName(name);
        mContact.setNumber(phoneNumber);
        mContact.setEmail(emailAddress);
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void setResult() {
        boolean empty = TextUtils.isEmpty(mName.getText()) ||
                TextUtils.isEmpty(mNumber.getText()) ||
                TextUtils.isEmpty(mEmail.getText());
        Intent i = new Intent();
        i.putExtra(EXTRA_EMPTY, empty);
        i.putExtra(EXTRA_CONTACT_ID, mContact.getId());
        getActivity().setResult(Activity.RESULT_OK, i);
    }
}

