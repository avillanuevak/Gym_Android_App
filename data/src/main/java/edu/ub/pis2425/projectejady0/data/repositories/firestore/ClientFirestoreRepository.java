package edu.ub.pis2425.projectejady0.data.repositories.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import edu.ub.pis2425.projectejady0.data.dtos.firestore.ClientFirestoreDto;
import edu.ub.pis2425.projectejady0.domain.Client;
import edu.ub.pis2425.projectejady0.data.mappers.DTOToDomainMapper;
import edu.ub.pis2425.projectejady0.domain.ClientId;
import edu.ub.pis2425.projectejady0.domain.ExerciseId;
import edu.ub.pis2425.projectejady0.domain.repositories.ClientRepository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import android.util.Log;

public class ClientFirestoreRepository implements ClientRepository {
    /* Constants */
    private static final String TAG = "ClientFirestoreRepo";
    private final CollectionReference clientsCollection;
    private String CLIENTS_COLLECTION_NAME = "Clients";
    private final FirebaseFirestore db;
    private final DTOToDomainMapper DTOToDomainMapper;

    private static ClientFirestoreRepository instance;

    private ClientFirestoreRepository() {
        db = FirebaseFirestore.getInstance();
        clientsCollection = db.collection("Clients");
        DTOToDomainMapper = new DTOToDomainMapper();
    }

    // Método público para obtener la única instancia
    public static synchronized ClientFirestoreRepository getInstance() {
        if (instance == null) {
            instance = new ClientFirestoreRepository();
        }
        return instance;
    }


    public CompletableFuture<List<Client>> getAllClients() {
        CompletableFuture<List<Client>> future = new CompletableFuture<>();
        clientsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<Client> clients = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    ClientFirestoreDto dto = document.toObject(ClientFirestoreDto.class);
                    clients.add(DTOToDomainMapper.map(dto, Client.class));
                }
                future.complete(clients);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }



    public void getClientById(String id, Callback<Client> callback) {
        db.collection(CLIENTS_COLLECTION_NAME)
                .document(id)
                .get()
                .addOnFailureListener(exception -> {
                    callback.onError(new Throwable("Error getting client"));
                })
                .addOnSuccessListener(ds -> {
                    if (ds.exists()) {
                        ClientFirestoreDto clientDto = ds.toObject(ClientFirestoreDto.class);
                        Client client = DTOToDomainMapper.map(clientDto, Client.class);
                        callback.onSuccess(client);
                    } else {
                        callback.onError(new Throwable("Client not found"));
                    }
                });
    }

    public void addClient(Client client, Callback<Void> callback) {
        ClientFirestoreDto clientDto = DTOToDomainMapper.map(client, ClientFirestoreDto.class);

        db.collection(CLIENTS_COLLECTION_NAME)
                .document(client.getId().toString())
                .set(clientDto)
                .addOnFailureListener(exception -> {
                    callback.onError(new Throwable("Error adding client"));
                })
                .addOnSuccessListener(ignored -> {
                    callback.onSuccess(null);
                });
    }

    public CompletableFuture<Void> updateClient(Client client) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        ClientFirestoreDto dto = DTOToDomainMapper.map(client, ClientFirestoreDto.class);
        clientsCollection.document(dto.getId()).set(dto).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(null);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public CompletableFuture<Void> deleteClient(String id) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        clientsCollection.document(id).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(null);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    @Override
    public Observable<Object> getByIdReactive(ClientId clientId) {
        return Observable.create(emitter -> {
            Log.d(TAG, "Fetching client with ID: " + clientId.toString());

            Task<DocumentSnapshot> task = db
                    .collection(CLIENTS_COLLECTION_NAME)
                    .document(clientId.toString())
                    .get();

            task.addOnFailureListener(exception -> {
                Log.e(TAG, "Database error fetching client: " + exception.getMessage());
                emitter.onError(new Throwable("Database error: " + exception.getMessage()));
            }).addOnSuccessListener(ds -> {
                if (ds.exists()) {
                    ClientFirestoreDto clientDto = ds.toObject(ClientFirestoreDto.class);
//          Client client = DTOToDomainMapper.map(clientDto, Client.class);
                    Client client = DTOToDomainMapper.map(clientDto, Client.class);

                    // Log the retrieved client info for debugging
                    if (client != null) {
                        Log.d(TAG, "Client found - ID: " + client.getId() +
                                ", Name: " + client.getUsername() +
                                ", Photo URL: " + client.getPhotoUrl());
                    }

                    if (client != null) {
                        Log.d(TAG, "Final client - Photo URL: " + client.getPhotoUrl());
                    }

                    emitter.onNext(client);
                    emitter.onComplete();
                } else {
                    Log.w(TAG, "Client not found with ID: " + clientId.toString());
                    emitter.onError(new Throwable("Client not found"));
                }
            });
        });
    }

    @Override
    public Completable addClientReactive(Client client) {
        return Completable.create(emitter -> {
            //    ClientFirestoreDto clientDto = DTOToDomainMapper.map(client, ClientFirestoreDto.class);
            ClientFirestoreDto clientDto = DTOToDomainMapper.map(client, ClientFirestoreDto.class);

            Task<Void> task = db
                    .collection(CLIENTS_COLLECTION_NAME)
                    .document(client.getId().toString())
                    .set(clientDto);

            task.addOnFailureListener(exception -> {
                emitter.onError(new Throwable("Error adding client"));
            }).addOnSuccessListener(ignored -> {
                emitter.onComplete();
            });
        });
    }

    @Override
    public Client getClientByUsername(String username) {
        CompletableFuture<Client> future = new CompletableFuture<>();
        clientsCollection.whereEqualTo("username", username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                ClientFirestoreDto dto = document.toObject(ClientFirestoreDto.class);
                future.complete(DTOToDomainMapper.map(dto, Client.class));
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future.join();
    }

    @Override
    public boolean containsKey(String username) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        clientsCollection.whereEqualTo("username", username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(!task.getResult().isEmpty());
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future.join();
    }
    @Override
    public Observable<Client> getByIdReactiveString(String clientId) {
        return Observable.create(emitter -> {
            Task<DocumentSnapshot> task = db
                    .collection(CLIENTS_COLLECTION_NAME)
                    .document(clientId)
                    .get();

            task.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    ClientFirestoreDto clientDto = documentSnapshot.toObject(ClientFirestoreDto.class);
                    Client client = DTOToDomainMapper.map(clientDto, Client.class);
                    emitter.onNext(client);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("Client not found"));
                }
            }).addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Observable<Client> getClientByUsernameReact(String username) {
        return Observable.create(emitter -> {
            clientsCollection.whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot != null && !snapshot.isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) snapshot.getDocuments().get(0);
                                ClientFirestoreDto dto = document.toObject(ClientFirestoreDto.class);
                                Client client = DTOToDomainMapper.map(dto, Client.class);
                                emitter.onNext(client);
                                emitter.onComplete();
                            } else {
                                emitter.onError(new Exception("Client not found"));
                            }
                        } else {
                            emitter.onError(task.getException() != null ? task.getException() : new Exception("Unknown error"));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Observable<Boolean> isExerciseRegistered(String clientId, ExerciseId ejercicioId) {
        return getByIdReactiveString(clientId)
                .map(client -> client.hasExercise(ejercicioId))
                .onErrorReturnItem(false);
    }

    @Override
    public Completable updateUserInfo(String userId, String newName, String newEmail, String newPassword, String newUrl) {
        return Completable.create(emitter -> {
            Map<String, Object> updates = new HashMap<>();

            // Log the update details
            Log.d(TAG, "Updating user info - ID: " + userId +
                    ", Name: " + newName +
                    ", Email: " + newEmail +
                    ", Has password: " + (newPassword != null) +
                    ", Photo URL: " + newUrl);

            if (newName != null) {
                updates.put("username", newName);
            }
            if (newEmail != null) {
                updates.put("email", newEmail);
            }
            if (newPassword != null) {
                updates.put("password", newPassword);
            }

            // Always update the photoUrl field, even if null (to allow deletion)
            updates.put("photoUrl", newUrl);

            // First fetch the current document to verify it exists
            db.collection(CLIENTS_COLLECTION_NAME)
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Document exists, proceed with update
                            db.collection(CLIENTS_COLLECTION_NAME)
                                    .document(userId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User info updated successfully");

                                        // Now verify the update by reading back the data
                                        db.collection(CLIENTS_COLLECTION_NAME)
                                                .document(userId)
                                                .get()
                                                .addOnSuccessListener(updatedDoc -> {
                                                    if (updatedDoc.exists()) {
                                                        ClientFirestoreDto dto = updatedDoc.toObject(ClientFirestoreDto.class);
                                                        if (dto != null) {
                                                            Log.d(TAG, "Updated values verified - " +
                                                                    "Name: " + dto.getUsername() + ", " +
                                                                    "Email: " + dto.getEmail() + ", " +
                                                                    "PhotoURL: " + dto.getPhotoUrl());
                                                        }
                                                        emitter.onComplete();
                                                    } else {
                                                        Log.e(TAG, "Document disappeared after update");
                                                        emitter.onComplete(); // Still complete since update succeeded
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w(TAG, "Failed to verify update, but update succeeded", e);
                                                    emitter.onComplete(); // Still complete since update succeeded
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating user info: " + e.getMessage());
                                        emitter.onError(new Throwable("Error updating user info: " + e.getMessage()));
                                    });
                        } else {
                            Log.e(TAG, "Cannot update - document doesn't exist: " + userId);
                            emitter.onError(new Throwable("Document not found with ID: " + userId));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking document existence: " + e.getMessage());
                        emitter.onError(new Throwable("Error checking document: " + e.getMessage()));
                    });
        });
    }


}