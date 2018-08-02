package com.amrat.googledriveapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.data.DataBufferIterator;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCreatePublicButton, mRewritePublicButton, mFetchPublicButton,
            mCreateAppButton, mRewriteAppButton, mFetchAppButton;
    private TextView mText;
    //    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private String TAG = "MainActivity";
    private final static int REQUEST_CODE_CREATE_APP_DATA = 1, REQUEST_CODE_REWRITE_APP_DATA = 2,
            REQUEST_CODE_FETCH_APP_DATA = 3, REQUEST_CODE_CREATE_PUBLIC_DATA = 4,
            REQUEST_CODE_REWRITE_PUBLIC_DATA = 5, REQUEST_CODE_FETCH_PUBLIC_DATA = 6;

    private TaskCompletionSource mOpenItemTaskSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreatePublicButton = findViewById(R.id.create_public_file);
        mRewritePublicButton = findViewById(R.id.rewrite_public_file);
        mFetchPublicButton = findViewById(R.id.fetch_public_file);
        mCreateAppButton = findViewById(R.id.create_app_file);
        mRewriteAppButton = findViewById(R.id.rewrite_app_file);
        mFetchAppButton = findViewById(R.id.fetch_app_file);
        mText = findViewById(R.id.text);

        mCreatePublicButton.setOnClickListener(this);
        mRewritePublicButton.setOnClickListener(this);
        mFetchPublicButton.setOnClickListener(this);
        mCreateAppButton.setOnClickListener(this);
        mRewriteAppButton.setOnClickListener(this);
        mFetchAppButton.setOnClickListener(this);

        mGoogleSignInClient = buildGoogleSignInClient();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mCreateAppButton.getId()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_CREATE_APP_DATA);
        } else if (v.getId() == mRewriteAppButton.getId()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_REWRITE_APP_DATA);
        } else if (v.getId() == mFetchAppButton.getId()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_FETCH_APP_DATA);
        } else if (v.getId() == mCreatePublicButton.getId()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_CREATE_PUBLIC_DATA);
        } else if (v.getId() == mRewritePublicButton.getId()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_REWRITE_PUBLIC_DATA);
        } else if (v.getId() == mFetchPublicButton.getId()) {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_FETCH_PUBLIC_DATA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {

//            if (requestCode == 10) {
//                if (resultCode == RESULT_OK) {
//                    DriveId driveId = intent.getParcelableExtra(
//                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
//                    Log.i(TAG, "DriveId success");
//                    mOpenItemTaskSource.setResult(driveId);
//                } else {
//                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
//                }
//                return;
//            }

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            Log.i(TAG, "Sign in success");
            mDriveClient = Drive.getDriveClient(getApplicationContext(), googleSignInAccount);
            mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), googleSignInAccount);


            if (requestCode == REQUEST_CODE_CREATE_APP_DATA) {

//                App File Creation
                createFileInAppFolder();

//                Normal File Creation
//                createFileOnDrive(mDriveResourceClient.getRootFolder(), mDriveResourceClient.createContents());

            } else if (requestCode == REQUEST_CODE_REWRITE_APP_DATA) {


//                App File Rewrite
                mDriveClient
                        .requestSync()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Sync Success", Toast.LENGTH_SHORT).show();
                                reWriteAppFileOnDrive();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Sync Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

////                Normal File Rewrite
//                mDriveClient
//                        .requestSync()
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(MainActivity.this, "Sync Success", Toast.LENGTH_SHORT).show();
//                                reWriteFileOnDrive();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(MainActivity.this, "Sync Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });

            } else if (requestCode == REQUEST_CODE_FETCH_APP_DATA) {

//                App File Fetch
                mDriveClient
                        .requestSync()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Sync Success", Toast.LENGTH_SHORT).show();
                                fetchAppFileOnDrive();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Sync Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

////                Normal File Fetch
//                mDriveClient
//                        .requestSync()
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(MainActivity.this, "Sync Success", Toast.LENGTH_SHORT).show();
//                                fetchFileOnDrive();
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(MainActivity.this, "Sync Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }

        } catch (
                ApiException apiException)

        {
            String message = apiException.getMessage();
            if (message == null || message.isEmpty()) {
                Log.w(TAG, "Sign in failed_" + apiException.getLocalizedMessage(), apiException);
            }
            Log.w(TAG, "Sign in failed_" + apiException.getLocalizedMessage(), apiException);
        }
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }


//  App Files

    private void createFileInAppFolder() {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = appFolderTask.getResult();
                        DriveContents contents = createContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();
                        try (Writer writer = new OutputStreamWriter(outputStream)) {
                            writer.write("{user{name:'Amrat Singh'}}");
                        }

                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("user.json")
                                .setMimeType("application/json")
                                .setStarred(true)
                                .build();

                        return mDriveResourceClient.createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(this, new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        Log.d(TAG, "Success create file");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create file", e);
                    }
                });
    }

    public void reWriteAppFileOnDrive() {


        Task<DriveFolder> driveFolderTask = mDriveResourceClient.getAppFolder();
        driveFolderTask
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<DriveFolder>() {
                    @Override
                    public void onSuccess(DriveFolder driveFolder) {
                        Log.e(TAG, "able to fetch driveFolder >> " + "OnSuccessListener");

                        Query query =
                                new Query.Builder()
                                        .addFilter(Filters.eq(SearchableField.TITLE, "user.json"))
                                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/json"))
                                        .addFilter(Filters.eq(SearchableField.STARRED, true))
                                        .addFilter(Filters.eq(SearchableField.TRASHED, false))
                                        .build();
                        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
                        queryTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                DataBufferIterator<Metadata> iterable = (DataBufferIterator<Metadata>) metadataBuffer.iterator();
                                if (iterable.hasNext()) {
                                    Metadata metadata = iterable.next();

                                    Task<DriveContents> openTask =
                                            mDriveResourceClient.openFile(metadata.getDriveId().asDriveFile(), DriveFile.MODE_WRITE_ONLY);

                                    rewriteAppData(openTask);
                                } else {
                                    Toast.makeText(MainActivity.this, "Don't exist.", Toast.LENGTH_SHORT).show();
                                }
                                Log.e(TAG, "able to modify file >> " + "addOnSuccessListener");
                            }
                        }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Unable to modify file", e);
                            }
                        });

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "able to fetch driveFolder >> " + "OnFailureListener");
                    }
                });
    }

    private void rewriteAppData(Task<DriveContents> openTask) {

        openTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents driveContents = task.getResult();
//                        ParcelFileDescriptor pfd = driveContents.getParcelFileDescriptor();
//                        long bytesToSkip = pfd.getStatSize();
//                        try (InputStream in = new FileInputStream(pfd.getFileDescriptor())) {
//                            // Skip to end of file
//                            while (bytesToSkip > 0) {
//                                long skipped = in.skip(bytesToSkip);
//                                bytesToSkip -= skipped;
//                            }
//                        }

                        try (OutputStream out = driveContents.getOutputStream()) {
                            out.flush();
                            out.write("{user{name:'Amrat Singh', stat:'chan'}}".getBytes());
                        }
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setStarred(true)
                                .setLastViewedByMeDate(new Date())
                                .build();
                        Task<Void> commitTask =
                                mDriveResourceClient.commitContents(driveContents, changeSet);
                        return commitTask;
                    }
                })
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "modify file success >> " + "addOnSuccessListener");
                        Toast.makeText(MainActivity.this, "modify file success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "file modify failure >> " + "addOnFailureListener", e);
                    }
                });
    }

    public void fetchAppFileOnDrive() {

        Task<DriveFolder> driveFolderTask = mDriveResourceClient.getAppFolder();
        driveFolderTask
                .addOnSuccessListener(this, new OnSuccessListener<DriveFolder>() {
                    @Override
                    public void onSuccess(DriveFolder driveFolder) {
                        Log.e(TAG, "able to fetch driveFolder >> " + "OnSuccessListener");


                        Query query =
                                new Query.Builder()
                                        .addFilter(Filters.eq(SearchableField.TITLE, "user.json"))
                                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/json"))
                                        .addFilter(Filters.eq(SearchableField.STARRED, true))
                                        .addFilter(Filters.eq(SearchableField.TRASHED, false))
                                        .build();

                        Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(driveFolder, query);


                        queryTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                DataBufferIterator<Metadata> iterable = (DataBufferIterator<Metadata>) metadataBuffer.iterator();
                                if (iterable.hasNext()) {
                                    Metadata metadata = iterable.next();

                                    if (metadata.isInAppFolder()) {
                                        Task<DriveContents> openTask =
                                                mDriveResourceClient.openFile(metadata.getDriveId().asDriveFile(), DriveFile.MODE_READ_ONLY);

                                        fetchAppData(openTask);
                                    }

                                    metadataBuffer.release();
                                } else {
                                    Toast.makeText(MainActivity.this, "Don't exist.", Toast.LENGTH_SHORT).show();
                                }
                                Log.e(TAG, "able to fetch file >> " + "addOnSuccessListener");
                            }
                        }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Unable to fetch file", e);
                            }
                        });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "able to fetch driveFolder >> " + "OnFailureListener");
                    }
                });
    }

    private void fetchAppData(Task<DriveContents> fetchFileTask) {
        fetchFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            Toast.makeText(MainActivity.this, "fetch file success see logs", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "fetch result >> " + builder.toString());
                            mText.setText(builder.toString());
                        }
                        Task<Void> discardTask = mDriveResourceClient.discardContents(contents);
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "fetch file failed", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Don't exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


//  Normal Files

    public void createFileOnDrive(final Task<DriveFolder> driveFolderTask, final Task<DriveContents> driveContentsTask) {
        Tasks.whenAll(driveFolderTask, driveContentsTask)
                .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                    @Override
                    public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                        DriveFolder parent = driveFolderTask.getResult();
                        DriveContents contents = driveContentsTask.getResult();
                        OutputStream outputStream = contents.getOutputStream();
                        try (Writer writer = new OutputStreamWriter(outputStream)) {
                            writer.write("{user{name:'Amrat Singh'}}");
                        }
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("user.json")
                                .setMimeType("application/json")
                                .setStarred(true)
                                .build();
                        return mDriveResourceClient.createFile(parent, changeSet, contents);
                    }
                })
                .addOnSuccessListener(this, new OnSuccessListener<DriveFile>() {
                    @Override
                    public void onSuccess(DriveFile driveFile) {
                        Log.d(TAG, "success create file >> " + driveFile.getDriveId().encodeToString());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Unable to create file", e);
                    }
                });
    }

    public void fetchFileOnDrive() {

        Query query =
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, "user.json"))
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/json"))
                        .addFilter(Filters.eq(SearchableField.STARRED, true))
                        .addFilter(Filters.eq(SearchableField.TRASHED, false))
                        .build();
        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
        queryTask.addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadataBuffer) {
                DataBufferIterator<Metadata> iterable = (DataBufferIterator<Metadata>) metadataBuffer.iterator();
                if (iterable.hasNext()) {
                    Metadata metadata = iterable.next();

                    Task<DriveContents> openTask =
                            mDriveResourceClient.openFile(metadata.getDriveId().asDriveFile(), DriveFile.MODE_READ_ONLY);

                    fetchData(openTask);

                    metadataBuffer.release();
                } else {
                    Toast.makeText(MainActivity.this, "Don't exist.", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "able to fetch file >> " + "addOnSuccessListener");
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to fetch file", e);
            }
        });
    }

    private void fetchData(Task<DriveContents> fetchFileTask) {
        fetchFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            Toast.makeText(MainActivity.this, "fetch file success", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "fetch result >> " + builder.toString());
                            mText.setText(builder.toString());
                        }
                        Task<Void> discardTask = mDriveResourceClient.discardContents(contents);
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "fetch file failed", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "Don't exist.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void reWriteFileOnDrive() {
        Query query =
                new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, "user.json"))
                        .addFilter(Filters.eq(SearchableField.MIME_TYPE, "application/json"))
                        .addFilter(Filters.eq(SearchableField.STARRED, true))
                        .addFilter(Filters.eq(SearchableField.TRASHED, false))
                        .build();
        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
        queryTask.addOnSuccessListener(this, new OnSuccessListener<MetadataBuffer>() {
            @Override
            public void onSuccess(MetadataBuffer metadataBuffer) {
                DataBufferIterator<Metadata> iterable = (DataBufferIterator<Metadata>) metadataBuffer.iterator();
                if (iterable.hasNext()) {
                    Metadata metadata = iterable.next();

                    Task<DriveContents> openTask =
                            mDriveResourceClient.openFile(metadata.getDriveId().asDriveFile(), DriveFile.MODE_WRITE_ONLY);

                    rewriteData(openTask);
                } else {
                    Toast.makeText(MainActivity.this, "Don't exist.", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "able to modify file >> " + "addOnSuccessListener");
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to modify file", e);
            }
        });
    }

    private void rewriteData(Task<DriveContents> openTask) {

        openTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents driveContents = task.getResult();
//                        ParcelFileDescriptor pfd = driveContents.getParcelFileDescriptor();
//                        long bytesToSkip = pfd.getStatSize();
//                        try (InputStream in = new FileInputStream(pfd.getFileDescriptor())) {
//                            // Skip to end of file
//                            while (bytesToSkip > 0) {
//                                long skipped = in.skip(bytesToSkip);
//                                bytesToSkip -= skipped;
//                            }
//                        }

                        try (OutputStream out = driveContents.getOutputStream()) {
                            out.flush();
                            out.write("{user{name:'Amrat Singh', stat:'chan'}}".getBytes());
                        }
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setStarred(true)
                                .setLastViewedByMeDate(new Date())
                                .build();
                        Task<Void> commitTask =
                                mDriveResourceClient.commitContents(driveContents, changeSet);
                        return commitTask;
                    }
                })
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "modify file success >> " + "addOnSuccessListener");
                        Toast.makeText(MainActivity.this, "modify file success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "file modify failure >> " + "addOnFailureListener", e);
                    }
                });
    }
}