package uk.ac.wlv.criminalintentweek06;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.UUID;
import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import java.util.Date;
import android.provider.ContactsContract;
import android.net.Uri;
import android.database.Cursor;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.io.File;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.content.pm.ResolveInfo;
import java.util.List;
import android.graphics.Bitmap;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_CONTACT_CALL = 2;

    private Button mCallContactButton;
    private String mContactPhoneNumber;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mDeleteButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACT_CALL && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor c = getActivity().getContentResolver().query(
                    contactUri, queryFields, null, null, null
            );

            try {
                if (c != null && c.moveToFirst()) {
                    mContactPhoneNumber = c.getString(0);
                    mCallContactButton.setText("Call " + mContactPhoneNumber);

                    Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + mContactPhoneNumber));
                    startActivity(dialIntent);
                }
            } finally {
                if (c != null) c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "uk.ac.wlv.criminalintentweek06.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private String getCrimeReport() {
        String solvedString = mCrime.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        // --- Important! Initialize mPhotoView FIRST
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        // Setup mPhotoView click to open full image
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    return;
                }
                Uri photoUri = FileProvider.getUriForFile(getActivity(),
                        "uk.ac.wlv.criminalintentweek06.fileprovider", mPhotoFile);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(photoUri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

        mCallContactButton = (Button) v.findViewById(R.id.call_contact_button);
        mCallContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContact = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(pickContact, REQUEST_CONTACT_CALL);
            }
        });

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Setup Camera Button
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        if (mPhotoButton != null) {
            mPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (captureImage.resolveActivity(getActivity().getPackageManager()) != null) {
                        Uri uri = FileProvider.getUriForFile(
                                getActivity(),
                                "uk.ac.wlv.criminalintentweek06.fileprovider",
                                mPhotoFile
                        );

                        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                        List<ResolveInfo> cameraActivities = getActivity()
                                .getPackageManager()
                                .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                        for (ResolveInfo activity : cameraActivities) {
                            getActivity().grantUriPermission(
                                    activity.activityInfo.packageName,
                                    uri,
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            );
                        }

                        startActivityForResult(captureImage, REQUEST_PHOTO);
                    }
                }
            });
        }

        Button mCallPoliceButton = (Button) v.findViewById(R.id.call_police_button);
        mCallPoliceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse(getString(R.string.police_number));
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        Button deleteButton = (Button) v.findViewById(R.id.crime_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            }
        });

        return v;
    }
}
