package com.example.hai.awaresys.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hai.awaresys.R;
import com.example.hai.awaresys.dao.AwareMessage;
import com.example.hai.awaresys.dao.ContactDao;
import com.example.hai.awaresys.dao.Mail;
import com.example.hai.awaresys.model.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hai on 3/24/2016.
 */
public class ContactListFragment extends Fragment {
    private ArrayList<Contact> mContacts;
    private ContactDao contactDao;
    private static final int REQUEST_NAME = 1;
    private ListView listView;
    private AwareMessage awareMessage;
    public static final String TAG = "ContactListFragment";
    public static final String WARNMESSAGE = "Dear care taker, the subject is experiencing high level frustration.\n";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.contact_title);
        mContacts = ContactLab.get(getActivity()).getmContacts();
        contactDao = new ContactDao(getActivity().getApplicationContext());
        awareMessage= new AwareMessage(getActivity());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_list, parent, false);
        listView = (ListView) v.findViewById(R.id.listView);
        ContactAdapter adapter = new ContactAdapter(mContacts);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.contact_list_item_context, menu);
                return true;
            }

            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_delete_contact:
                        ContactAdapter adapter = (ContactAdapter) listView.getAdapter();
                        ContactLab contactLab = ContactLab.get(getActivity());
                        for (int i = adapter.getCount() - 1; i >= 0; i--) {
                            if (listView.isItemChecked(i)) {
                                Contact deleted = adapter.getItem(i);
                                contactDao.delete(deleted.getId());
                                contactLab.deleteContact(deleted);
                            }
                        }
                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        FloatingActionButton fabAddress = (FloatingActionButton) v.findViewById(R.id.fabAddress);
        fabAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                String address = getAddress(location.getLatitude(), location.getLongitude());
                if(awareMessage.sendEmails(address) && awareMessage.sendMessages(address))
                toastMessage(R.drawable.success,"Current Address Sent");
                else
                    toastMessage(R.drawable.fail,"Current Address not Sent");
            }
        });

        FloatingActionButton fabMessage = (FloatingActionButton) v.findViewById(R.id.fabMessage);
        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awareMessage.sendEmails(WARNMESSAGE) && awareMessage.sendMessages(WARNMESSAGE))
                toastMessage(R.drawable.success,"Message & Email Sent");
                else
                toastMessage(R.drawable.fail,"Message & Email not Sent");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact c = ((ContactAdapter) listView.getAdapter()).getItem(position);
                Intent i = new Intent(getActivity(), ContactPagerActivity.class);
                i.putExtra(ContactFragment.EXTRA_CONTACT_ID, c.getId());
                startActivityForResult(i, 0);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NAME) {
            boolean empty = data.getBooleanExtra(ContactFragment.EXTRA_EMPTY, false);
            String id = data.getStringExtra(ContactFragment.EXTRA_CONTACT_ID);
            if (empty) {
                toastMessage(R.drawable.fail, "Fill In All Fields");
                ContactLab.get(getActivity()).deleteContact(id);
                contactDao.delete(id);
                Log.d(TAG, "empty remove");
            }
        }
        ((ContactAdapter) listView.getAdapter()).notifyDataSetChanged();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_contact:
                Contact contact = new Contact();
                ContactLab.get(getActivity()).addContact(contact);
                Intent i = new Intent(getActivity(), ContactActivity.class);
                i.putExtra(ContactFragment.EXTRA_CONTACT_ID, contact.getId());
                startActivityForResult(i, REQUEST_NAME);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_contact_list, menu);

    }

    public String getAddress(double lat, double lon)
    {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Current Address:\n");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                ret = strReturnedAddress.toString();
            }
            else{
                ret = "Current Address:\n"+"Latitude: " + lat + "\nLongitude: " + lon+"\n";

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Current Address:\n"+"Latitude: " + lat + "\nLongitude: " + lon+"\n";

        }
        return ret;
    }

    private class ContactAdapter extends ArrayAdapter<Contact> {
        public ContactAdapter(ArrayList<Contact> contacts) {
            super(getActivity(), android.R.layout.simple_list_item_1, contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // if we weren't given a view, inflate one
            if (null == convertView) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_contact, null);
            }
            // configure the view for this Contact
            Contact c = getItem(position);
            TextView nameTextView =
                    (TextView) convertView.findViewById(R.id.contact_list_item_nameTextView);
            nameTextView.setText(c.getName());
            return convertView;
        }

    }
}
