package com.example.hai.awaresys.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hai.awaresys.R;
import com.example.hai.awaresys.dao.SkypeDao;
import com.example.hai.awaresys.model.Skype;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PhotoGalleryFragment extends Fragment{
    private GridView mGridView;
    private ArrayList<Skype> mSkypes;
    private SkypeDao skypeDao;
    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;
    public static final int REQUEST_DELETE = 2;
    public static final String TAG = "PhotoGalleryFragment";
    public SkypeAdapter skypeAdapter;
    Bitmap photo;
    public String directory;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSkypes = SkypeLab.get(getActivity()).getmSkypes();
        skypeDao = new SkypeDao(getActivity());
        directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AwareSys";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        setupAdapter();
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                final String skypeId = ((Skype)parent.getItemAtPosition(position)).getId();
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_delete_skype, null);
                final EditText skypeAccount = (EditText) dialogView.findViewById(R.id.skypeAccountEditText);
                skypeAccount.setText(((Skype)parent.getItemAtPosition(position)).getAccount());
                Button save = (Button) dialogView.findViewById(R.id.saveSkypeButton);
                Button delete = (Button) dialogView.findViewById(R.id.deleteSkypeButton);
                save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Skype s = SkypeLab.get(getActivity()).getSkype(skypeId);
                        String account = skypeAccount.getText().toString().trim();
                        s.setAccount(account);
                        skypeDao.updateAccount(s);
                        alertDialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        SkypeLab.get(getActivity()).deleteSkype(skypeId);
                        skypeDao.delete(skypeId);
                        setupAdapter();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
                return true;
                }

        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String account = ((Skype)parent.getItemAtPosition(position)).getAccount();
                String skypeURI = "skype:" + account + "?call&video=true";
                initiateSkypeUri(getContext(), skypeURI);

            }
        });
        return v;
    }

    public void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mSkypes != null) {
            skypeAdapter = new SkypeAdapter(mSkypes);
            mGridView.setAdapter(skypeAdapter);
        } else {
            mGridView.setAdapter(null);
        }

    }


    private void selectImage() {
        final CharSequence[] items = { "Camera", "Photos", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Photos")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Photo"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap image = null;
            if (requestCode == SELECT_FILE)
                image = onGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                image = onCameraResult(data);
            String imageName = saveToExternalStorage(image);
            createNewSkype(imageName);
        }
        setupAdapter();
    }

    public void createNewSkype(String imageName){
        Skype s = new Skype();
        s.setPath(imageName);
        mSkypes.add(s);
        skypeDao.add(s);
    }

    public Bitmap onCameraResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        return thumbnail;
    }


    private Bitmap onGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        return bm;
    }

    private String saveToExternalStorage(Bitmap bitmapImage){
        String imageName = System.currentTimeMillis()+".jpg";
        File myDir = new File(directory);
        myDir.mkdirs();
        File myPath = new File(directory, imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageName;
    }

    private Bitmap loadImageFromStorage(String imageName)
    {
        try {
            File f=new File(directory,imageName);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void initiateSkypeUri(Context myContext, String mySkypeUri) {

        // Make sure the Skype for Android client is installed
        if (!isSkypeClientInstalled(myContext)) {
            goToMarket(myContext);
            return;
        }

        // Create the Intent from our Skype URI
        Uri skypeUri = Uri.parse(mySkypeUri);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);

        // Restrict the Intent to being handled by the Skype for Android client
        // only
        myIntent.setComponent(new ComponentName("com.skype.raider",
                "com.skype.raider.Main"));
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Initiate the Intent. It should never fail since we've already
        // established the
        // presence of its handler (although there is an extremely minute window
        // where that
        // handler can go away...)
        myContext.startActivity(myIntent);

        return;
    }

    /**
     * Determine whether the Skype for Android client is installed on this device
     */


    public boolean isSkypeClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.skype.raider",
                    PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }

    /**
     * Install the Skype client through the market: URI scheme.
     *
     * @param myContext
     */

    public void goToMarket(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=com.skype.raider");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);

        return;
    }


    private class SkypeAdapter extends ArrayAdapter<Skype> {
        public SkypeAdapter(ArrayList<Skype> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.gallery_item, parent, false);
            }

            Skype item = getItem(position);
            ImageView imageView = (ImageView) convertView
                    .findViewById(R.id.gallery_item_imageView);
            photo = loadImageFromStorage(item.getPath());
            if(photo != null)
                imageView.setImageBitmap(photo);
            return convertView;
        }
    }
}
