package edu.ub.pis2425.projecte;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ub.pis2425.projecte.databinding.ActivityProfileBinding;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.ClientFirestoreRepository;
import edu.ub.pis2425.projectejady0.data.repositories.firestore.NutritionPlanFirestoreRepository;
import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projectejady0.domain.NutritionCalculationResult;
import edu.ub.pis2425.projectejady0.domain.NutritionPlan;
import edu.ub.pis2425.projectejady0.features.CalculateNutritionPlanUseCase;
import edu.ub.pis2425.projectejady0.features.UCChangeClientInfo;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ProfileActivity";

    private ActivityProfileBinding binding;

    private ProfileViewModel profileViewModel;
    private Uri selectedProfilePictureUri;
    private boolean isImageDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d(TAG, "ProfileActivity created");

        initViewModel();

        NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationHelper.setupNavigation();

        initWidgetListeners();

        fetchUserData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ProfileActivity resumed");

        if (profileViewModel.getUserInfoLiveData().getValue() != null) {
            Client currentUser = profileViewModel.getUserInfoLiveData().getValue().getData();
            if (currentUser != null) {
                loadProfileImage(currentUser.getPhotoUrl());
            }
        }

        fetchUserData();
    }

    private void observeUserInfo() {
        profileViewModel.getUserInfoLiveData().observe(this, stateData -> {
            if (stateData != null) {
                switch (stateData.getStatus()) {
                    case SUCCESS:
                        Client user = stateData.getData();
                        if (user != null) {
                            binding.tvCurrentName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
                            binding.tvCurrentEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                            binding.tvCurrentPassword.setText(user.getPassword() != null ? "••••••••" : "N/A");
                            loadProfileImage(user.getPhotoUrl());
                        }
                        break;
                    case ERROR:
                        Log.e(TAG, "Error loading user info: " + stateData.getError().getMessage());
                        Toast.makeText(this, "Error loading user info", Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        Log.d(TAG, "Loading user info...");
                        break;
                    default:
                        break;
                }
            }
        });

        profileViewModel.getUpdateStatus().observe(this, status -> {
            if ("Profile updated successfully".equals(status)) {
                Log.d(TAG, "Profile updated successfully");
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                isImageDeleted = false;
                selectedProfilePictureUri = null;
                fetchUserData();
            } else if (status != null && status.startsWith("Error")) {
                Log.e(TAG, "Error updating profile: " + status);
                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage(String photoUrl) {
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Log.d(TAG, "Loading profile image from URL: " + photoUrl);
            String uniqueUrl = photoUrl + "?timestamp=" + System.currentTimeMillis();

            Picasso.get()
                    .invalidate(photoUrl);

            Picasso.get()
                    .load(uniqueUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(binding.profilePicture, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Profile image loaded successfully");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Error loading profile image: " + e.getMessage(), e);
                            Picasso.get()
                                    .load(photoUrl)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .into(binding.profilePicture);
                        }
                    });
        } else {
            Log.d(TAG, "No profile image URL available, using placeholder");
            binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }

    private void fetchUserData() {
        String userId = SessionManager.getInstance(this).getUserId();
        Log.d(TAG, "Fetching user data for ID: " + userId);
        profileViewModel.getUserInfoLiveData().postValue(null);
        profileViewModel.getUserInfo(userId);
    }

    private void initWidgetListeners() {
        binding.btnUploadPicture.setOnClickListener(v -> openImagePicker());

        binding.btnDeletePicture.setOnClickListener(v -> {
            isImageDeleted = true;
            selectedProfilePictureUri = null;
            binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder);
            updateProfileWithImageChanges();
            Toast.makeText(this, "Profile picture deleted", Toast.LENGTH_SHORT).show();
        });

        binding.btnEditName.setOnClickListener(v -> showEditDialog("name", binding.tvCurrentName.getText().toString()));
        binding.btnEditEmail.setOnClickListener(v -> showEditDialog("email", binding.tvCurrentEmail.getText().toString()));
        binding.btnEditPassword.setOnClickListener(v -> showEditDialog("password", ""));
    }

    private void showEditDialog(String fieldType, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_field, null);
        builder.setView(dialogView);

        EditText etEditField = dialogView.findViewById(R.id.et_edit_field);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        if (fieldType.equals("password")) {
            etEditField.setHint("Enter new password");
            etEditField.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            etEditField.setText(currentValue);
            if (fieldType.equals("email")) {
                etEditField.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            String newValue = etEditField.getText().toString().trim();
            if (!newValue.isEmpty()) {
                updateField(fieldType, newValue);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateField(String fieldType, String newValue) {
        String userId = SessionManager.getInstance(this).getUserId();
        Client currentUser = profileViewModel.getUserInfoLiveData().getValue().getData();

        if (currentUser != null) {
            String currentName = currentUser.getUsername();
            String currentEmail = currentUser.getEmail();
            String currentPassword = currentUser.getPassword();
            String currentPhotoUrl = currentUser.getPhotoUrl();

            switch (fieldType) {
                case "name":
                    currentName = newValue;
                    binding.tvCurrentName.setText(newValue);
                    break;
                case "email":
                    currentEmail = newValue;
                    binding.tvCurrentEmail.setText(newValue);
                    break;
                case "password":
                    currentPassword = newValue;
                    binding.tvCurrentPassword.setText("••••••••");
                    break;
            }

            // If there's a pending image change, handle it
            if (selectedProfilePictureUri != null) {
                uploadImageAndSaveProfile(selectedProfilePictureUri, userId, currentName, currentEmail, currentPassword);
            } else if (isImageDeleted) {
                profileViewModel.saveProfile(userId, currentName, currentEmail, currentPassword, null);
            } else {
                profileViewModel.saveProfile(userId, currentName, currentEmail, currentPassword, currentPhotoUrl);
            }
        }
    }

    private void updateProfileWithImageChanges() {
        String userId = SessionManager.getInstance(this).getUserId();
        Client currentUser = profileViewModel.getUserInfoLiveData().getValue().getData();

        if (currentUser != null) {
            if (selectedProfilePictureUri != null) {
                uploadImageAndSaveProfile(selectedProfilePictureUri, userId,
                        currentUser.getUsername(), currentUser.getEmail(), currentUser.getPassword());
            } else if (isImageDeleted) {
                profileViewModel.saveProfile(userId,
                        currentUser.getUsername(), currentUser.getEmail(), currentUser.getPassword(), null);
            }
        }
    }

    private void initViewModel() {
        // Initialize repositories
        ClientFirestoreRepository clientRepository = ClientFirestoreRepository.getInstance();

        // Initialize use cases
        UCChangeClientInfo changeClientInfoUseCase = new UCChangeClientInfo(clientRepository);

        ProfileViewModelFactory factory = new ProfileViewModelFactory(
                changeClientInfoUseCase,
                null,
                null
        );

        profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
        observeUserInfo();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedProfilePictureUri = data.getData();
            isImageDeleted = false;

            Log.d(TAG, "Image selected from picker: " + selectedProfilePictureUri);

            try {
                Picasso.get()
                        .load(selectedProfilePictureUri)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(binding.profilePicture);

                // Immediately update profile with the new image
                updateProfileWithImageChanges();
            } catch (Exception e) {
                Log.e(TAG, "Error loading selected image", e);
                binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }
    }

    private void uploadImageAndSaveProfile(Uri imageUri, String userId, String name, String email, String password) {
        if (imageUri != null) {
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

            String timestamp = String.valueOf(System.currentTimeMillis());
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("profile_pictures/" + userId + "_" + timestamp + ".jpg");

            Log.d(TAG, "Starting upload to path: " + storageRef.getPath());

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Log.d(TAG, "Image uploaded successfully. Full URL: " + downloadUrl);
                            profileViewModel.saveProfile(userId, name, email, password, downloadUrl);
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL", e);
                            Toast.makeText(this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Upload failed", e);
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
