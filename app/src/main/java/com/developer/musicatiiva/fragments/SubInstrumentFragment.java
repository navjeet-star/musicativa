package com.developer.musicatiiva.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.musicatiiva.R;
import com.developer.musicatiiva.activities.MainActivity;
import com.developer.musicatiiva.adapters.InstrumentAdapters;
import com.developer.musicatiiva.apiModels.JsonPlaceHolderApi;
import com.developer.musicatiiva.commonClasses.CommonDialogs;
import com.developer.musicatiiva.commonClasses.CommonMethods;
import com.developer.musicatiiva.commonClasses.Validations;
import com.developer.musicatiiva.databinding.FragmentSubInstrumentBinding;
import com.developer.musicatiiva.models.AddSubInstrument;
import com.developer.musicatiiva.models.Instrument;
import com.developer.musicatiiva.models.InstrumentsList;
import com.developer.musicatiiva.utils.Constants;
import com.developer.musicatiiva.utils.MySharedPreferences;
import com.developer.musicatiiva.utils.SwipeToDeleteCallback;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.developer.musicatiiva.utils.Constants.INSTRUMENT_DATA_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubInstrumentFragment extends Fragment {

    FragmentSubInstrumentBinding fragmentSubInstrumentBinding;
    View view;
    List<Instrument> mainInstrumentList=new ArrayList<>();
    Context context;
    private int id;
    InstrumentAdapters instrumentAdapters;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    SwipeToDeleteCallback swipeToDeleteCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        fragmentSubInstrumentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sub_instrument, container, false);
        view = fragmentSubInstrumentBinding.getRoot();
        fragmentSubInstrumentBinding.setData(this);

        context = getActivity();
        id = getArguments().getInt(Constants.ID);

 //        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//
//        httpClient.connectTimeout(100, TimeUnit.SECONDS);
//        httpClient.readTimeout(100,TimeUnit.SECONDS);
//        httpClient.writeTimeout(100,TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(INSTRUMENT_DATA_URL)

                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        if (context != null) {

            ((MainActivity) context).activityMainBinding.iTollbar.tvActivities.setVisibility(View.GONE);
            ((MainActivity) context).activityMainBinding.iTollbar.tvAppName.setVisibility(View.VISIBLE);

            ((MainActivity) context).setTittle("Sub Categories");
            ((MainActivity) context).setMenuVisibilty(false);
            ((MainActivity) context).activityMainBinding.iTollbar.layoutTimer.setVisibility(View.GONE);

        }

         swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                instrumentAdapters.deleteSubInstrument(viewHolder.getAdapterPosition());

            }
        };
        adapterData();

        return view;
    }

    private void adapterData() {

        getListOfInstruments();

    }


    public void getListOfInstruments() {
        fragmentSubInstrumentBinding.textViewNoDataFound.setVisibility(View.GONE);
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    int user_id = MySharedPreferences.getInstance().getUserID(context, Constants.USERID);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty(Constants.ID, id);
                    jsonObject.addProperty(Constants.UID, user_id);
                    Call<InstrumentsList> call = jsonPlaceHolderApi.getSubInstrumentData(jsonObject);
                    call.enqueue(new Callback<InstrumentsList>() {
                        @Override
                        public void onResponse(Call<InstrumentsList> call, Response<InstrumentsList> response) {
                            if (response.isSuccessful()) {
                                InstrumentsList instrumentsList = response.body();

                                String description = instrumentsList.getDescription();
                                int status_code = instrumentsList.getStatus_code();
                                List<Instrument> instrumentList1 = instrumentsList.getData();

                                if (status_code == 1) {
                                    fragmentSubInstrumentBinding.buttonAddSubCategory.setVisibility(View.VISIBLE);
                                    fragmentSubInstrumentBinding.textViewNoDataFound.setVisibility(View.GONE);
                                    if (instrumentList1 != null) {
                                        if(mainInstrumentList!=null)
                                            mainInstrumentList.clear();
                                        mainInstrumentList=instrumentList1;
                                        instrumentAdapters = new InstrumentAdapters(context, instrumentList1);
                                        fragmentSubInstrumentBinding.instrumentsRecyclerview.setLayoutManager(new LinearLayoutManager(context));
                                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
                                        itemTouchHelper.attachToRecyclerView(fragmentSubInstrumentBinding.instrumentsRecyclerview);

                                        fragmentSubInstrumentBinding.instrumentsRecyclerview.setAdapter(instrumentAdapters);
                                    }


                                    // finish();
                                } else {
                                    Log.d("mohit", "onResponse: Status code : " + status_code + "\n" + "Description : " + description);
                                    Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                                    fragmentSubInstrumentBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                    fragmentSubInstrumentBinding.buttonAddSubCategory.setVisibility(View.GONE);
                                }


                            } else {
                                Log.d("mohit", "onResponse: " + response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();
                                fragmentSubInstrumentBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                                fragmentSubInstrumentBinding.textViewNoDataFound.setText(response.message());
                                fragmentSubInstrumentBinding.buttonAddSubCategory.setVisibility(View.GONE);

                            }
                            CommonDialogs.getInstance().dismissProgressDialog();


                        }

                        @Override
                        public void onFailure(Call<InstrumentsList> call, Throwable t) {
                            Log.d("mohit", "Error code: " + t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                            fragmentSubInstrumentBinding.textViewNoDataFound.setVisibility(View.VISIBLE);
                            fragmentSubInstrumentBinding.textViewNoDataFound.setText(t.getMessage());
                            fragmentSubInstrumentBinding.buttonAddSubCategory.setVisibility(View.GONE);


                        }
                    });
                }
            }
        });


    }





    ImageView mIvAddPhoto;
    ImageView mIvRemovePhoto;

    Dialog dialogAddSubInstrument;
    EditText mEdSubInstrTitle;
    String sub_instrument_image_path = "";
    File mSubInstrumentImageFile = null;

    public void addSubInstrument() {
        mSubInstrumentImageFile = null;
        dialogAddSubInstrument = new Dialog(context);
        dialogAddSubInstrument.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddSubInstrument.setContentView(R.layout.add_sub_instrument);
        dialogAddSubInstrument.setCanceledOnTouchOutside(true);
        dialogAddSubInstrument.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogAddSubInstrument.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mEdSubInstrTitle = dialogAddSubInstrument.findViewById(R.id.editTextTitle);
        mEdSubInstrTitle.requestFocus();
        dialogAddSubInstrument.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mIvAddPhoto = dialogAddSubInstrument.findViewById(R.id.add_photo_icon);
        mIvRemovePhoto = dialogAddSubInstrument.findViewById(R.id.remove_photo);
        TextView mTvCancel = dialogAddSubInstrument.findViewById(R.id.textViewCancel);

        TextView mTvSave = dialogAddSubInstrument.findViewById(R.id.textViewSave);
        mIvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubInstrumentImageFile = null;
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permission, 200);
                } else {
                     Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 100);

                    //Toast.makeText(context,"Please grant all necessary permissions..!!",Toast.LENGTH_LONG).show();

                }


            }
        });
        mIvRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubInstrumentImageFile = null;
                mIvRemovePhoto.setVisibility(View.GONE);
                mIvAddPhoto.setImageResource(R.drawable.add_instrument);
            }
        });
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddSubInstrument.dismiss();
            }
        });
        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        dialogAddSubInstrument.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
//
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 100);
            } else {
                 Toast.makeText(context, "Please grant all necessary permissions..!!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return getDriveFilePath( uri,context);

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }



    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
     //   return "com.google.android.apps.photos.content".equals(uri.getAuthority()) ||
      //   "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());

        return true;
    }






































Uri imagUri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch (requestCode) {
            case 100:

                if (resultCode == Activity.RESULT_OK) {

                    Uri uri=data.getData();
                    imagUri=uri;

                    sub_instrument_image_path=getPathFromUri(requireContext(),uri);
                    //sub_instrument_image_path=getRealPathFromURI(uri);

                    if (sub_instrument_image_path!= null){
                        mSubInstrumentImageFile=new File(sub_instrument_image_path);

                        Glide.with(context)
                                .load(sub_instrument_image_path)
                                .into(mIvAddPhoto);
                        mIvRemovePhoto.setVisibility(View.VISIBLE);
                    }



                }

                break;
        }
    }

    String sub_instrument_name="";

    private void save()
    {
        sub_instrument_name=mEdSubInstrTitle.getText().toString().trim();
        if (!Validations.isEmpty(sub_instrument_name)) {
            mEdSubInstrTitle.setError("Please enter title");
            return;
        }

       dialogAddSubInstrument.dismiss();
        new CommonMethods.InternetCheck(context, new CommonMethods.Consumer() {
            @Override
            public void accept(Boolean internet) {
                if (internet) {
                    CommonDialogs.getInstance().showProgressDialog(context);
                    int user_id= MySharedPreferences.getInstance().getUserID(context,Constants.USERID);
                    MultipartBody.Part fileToUpload;
                    RequestBody imagee=null;
                    if(mSubInstrumentImageFile==null)
                    {

                        RequestBody image = RequestBody.create(MediaType.parse("image/*"), "");
                        fileToUpload = MultipartBody.Part.createFormData("image", "", image);
                    }
                    else
                    {

                        imagee = RequestBody.create(okhttp3.MediaType.parse("image/*"), mSubInstrumentImageFile);
                         fileToUpload = MultipartBody.Part.createFormData("image", mSubInstrumentImageFile.getName(), imagee);
                    }

                    RequestBody uid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(user_id));
                    RequestBody instrument_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
                    RequestBody sub_instrument_name = RequestBody.create(MediaType.parse("text/plain"), mEdSubInstrTitle.getText().toString());






                    Call<AddSubInstrument> call = jsonPlaceHolderApi.addSubInstrument(fileToUpload,uid,instrument_id,sub_instrument_name);
                    call.enqueue(new Callback<AddSubInstrument>() {
                        @Override
                        public void onResponse(Call<AddSubInstrument> call, Response<AddSubInstrument> response) {
 //                            JsonObject jsonObject=response.body();
//                            Log.d(TAG, "onResponse: image "+jsonObject.get("image"));
//                            Log.d(TAG, "onResponse: "+jsonObject.get("user_id"));
//                            Log.d(TAG, "onResponse: "+jsonObject.get("intrumentId"));
//                            Log.d(TAG, "onResponse: "+jsonObject.get("subIntrumentName"));
//  //"status":200
                            //"user_id":"45"
                            //"intrumentId":"18"
                            //"subIntrumentName":"whwh"
                            // "image":{"name":"IMG_20200505_153220.jpg","type":"","tmp_name":"","error":1,"size":0}

                            //Log.d(TAG, "onResponse: body "+response.body().getStatus_code()+response.body().getDescription());
                            if(response.isSuccessful())
                            {
                                AddSubInstrument subInstrument=response.body();
                                //Log.d(TAG, "onResponse: sub instrument "+subInstrument);
                                String description=subInstrument.getDescription();

                                int status_code=subInstrument.getStatus_code();


                                if(status_code==1)
                                {
                                    getListOfInstruments();
                                    // finish();
                                }
                                else
                                {
                                     Toast.makeText(context, description, Toast.LENGTH_SHORT).show();

                                }



                            }
                            else
                            {
                                Log.d("mohit", "onResponse: "+response.message());
                                Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show();


                            }
                            CommonDialogs.getInstance().dismissProgressDialog();




                        }

                        @Override
                        public void onFailure(Call<AddSubInstrument> call, Throwable t) {
                            Log.d("mohit","Error code: "+t.getMessage());
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            CommonDialogs.getInstance().dismissProgressDialog();
                        }
                    });

                }
            }
        });
    }


}
