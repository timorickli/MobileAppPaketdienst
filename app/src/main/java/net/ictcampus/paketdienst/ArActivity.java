package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;

/**
 * Class to show 3d Modells in a AR View
 */
public class ArActivity extends AppCompatActivity implements Node.OnTapListener {
    private ModelRenderable activeRenderable;
    private static ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private Node mailbox, singlePackage, multiPackage, wagonPackage, activeNode;
    private static final long DELIVERY_TIME = 60 * 20 * 1000;
    private CountDownTimer countDownTimer;
    private long endTime, timeLeft;
    private boolean placed;
    private ArFragment arFragment;
    private boolean timerRunning;
    private Intent intent;
    private int id;
    private SharedPreferences inventoryFile, timers;
    private SharedPreferences.Editor editor;

    /**
     * Prepares everything for ARView, when activity is started
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        //Initialization of all important variables
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        timers = getSharedPreferences("Timers", Context.MODE_PRIVATE);
        intent = new Intent(getApplicationContext(), MapActivity.class);
        id = getIntent().getIntExtra("id", 0);
        Button btnBack = findViewById(R.id.back);
        editor = inventoryFile.edit();
        placed = false;

        //Chooses which model gets loaded
        setupModel();

        //ClickListener for Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Return previous PackageMark spots
                if (getIntent().getParcelableArrayListExtra("location") != null) {
                    intent.putExtra("location", getIntent().getParcelableArrayListExtra("location"));
                }

                //New activity without transition
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    /**
     * After onCreate, timer gets initialized/prepared
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Gets previous timer values
        timeLeft = timers.getLong("millisLeft", DELIVERY_TIME);
        timerRunning = timers.getBoolean("timerRunning", false);

        //Checks timer state
        if (timerRunning) {
            endTime = timers.getLong("endTime", 0);
            timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
            } else {
                startDeliveryTimer();
            }
        }
    }

    /**
     * On Camera Move, Frame Updates and methode gets executed
     *
     * @param frameTime
     */
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        //In case there's no frame
        if (frame == null) {
            return;
        }

        //Used to Create Model at a certain Point on the Floor automatically
        if (frame.getCamera().getTrackingState() == TrackingState.TRACKING && !placed) {
            Pose pos = frame.getCamera().getPose().compose(Pose.makeTranslation(1f, 0f, -2.5f));

            //Anchor Node to fix the Model in place
            Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pos);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            createModel(anchorNode, activeNode, activeRenderable);
        }
    }

    /**
     * Sets up model for rendering
     */
    private void setupModel() {
        //Chooses which model gets prepared
        switch (id) {
            case 2:
                singlePackage = new Node();
                activeNode = singlePackage;

                //Builder for model
                ModelRenderable.builder()
                        .setSource(ArActivity.this, R.raw.package_solo)
                        .build().thenAccept(renderable -> activeRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ", Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;

            case 3:
                multiPackage = new Node();
                activeNode = multiPackage;

                //Builder for model
                ModelRenderable.builder()
                        .setSource(ArActivity.this, R.raw.package_multi_new)
                        .build().thenAccept(renderable -> activeRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ", Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;

            case 4:
                wagonPackage = new Node();
                activeNode = wagonPackage;

                //Builder for model
                ModelRenderable.builder()
                        .setSource(ArActivity.this, R.raw.package_car_new)
                        .build().thenAccept(a -> activeRenderable = a)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ", Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;

            default:
                mailbox = new Node();
                activeNode = mailbox;

                //Builder for model
                ModelRenderable.builder()
                        .setSource(ArActivity.this, R.raw.mailbox)
                        .build().thenAccept(renderable -> activeRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ", Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;
        }
    }

    /**
     * Loads model into the view
     *
     * @param anchorNode Point, where its fixed
     * @param node       Model
     * @param renderable Renderable part of the model
     */
    private void createModel(AnchorNode anchorNode, Node node, Renderable renderable) {
        if (activeNode == mailbox) {
            //Resize
            node.setLocalScale(new Vector3(0.7f, 0.7f, 0.7f));
        } else if (activeNode == wagonPackage) {
            //Resize
            node.setLocalScale(new Vector3(60f, 60f, 60f));
        } else if (activeNode == multiPackage) {
            //Resize
            node.setLocalScale(new Vector3(40f, 40f, 40f));
        } else {
            //Resize
            node.setLocalScale(new Vector3(15f, 15f, 15f));
        }
        //Rotate
        node.setLocalRotation(Quaternion.multiply(Quaternion.axisAngle(new Vector3(0.3f, 0, 1f), 90f), Quaternion.axisAngle(new Vector3(0, 1f, 0), 180f)));

        //Set Anchor, Renderable, TapListener
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.setOnTapListener(this);
        placed = true;
    }

    /**
     * TapListener for the loaded model
     *
     * @param hitTestResult The model, which got hit
     * @param motionEvent   Kind of motion
     */
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        //Convert hitresult to node
        Node hitNode = hitTestResult.getNode();

        //Statements to decide, which model got hit (Switch was not possible)
        if (hitNode == mailbox) {

            //Looks, if there are any Packages collected
            if (inventoryFile.getInt("PACKAGES", 0) != 0) {
                Toast.makeText(ArActivity.this, "Du hast das Paket abgegeben", Toast.LENGTH_SHORT).show();

                //Deletes model from view and deletes Anchor
                arFragment.getArSceneView().getScene().removeChild(hitNode);
                hitNode.setParent(null);
                hitNode = null;

                //Edit Inventory
                editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) + 10 * inventoryFile.getInt("MULTIPLIER", 1)).apply();
                editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) - 1).apply();

                intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
                markerOptions = getIntent().getParcelableArrayListExtra("locationMailBox");
                markerOptions.clear();
                intent.putExtra("location", markerOptions);
            }

            //In case a bug happened
            else {
                Toast.makeText(ArActivity.this, "Du hast kein passendes Paket zum abgeben, hol dir eins bevor du wieder kommst", Toast.LENGTH_SHORT).show();
                intent.putExtra("location", getIntent().getParcelableArrayListExtra("location"));
                startDeliveryTimer();
            }
        } else if (hitNode == singlePackage) {
            Toast.makeText(ArActivity.this, "Du hast ein einzelnes Paket aufgesammelt", Toast.LENGTH_SHORT).show();

            //Deletes model from view and deletes Anchor
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;

            //Edit Inventory
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 1).apply();

            intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
            startDeliveryTimer();
        } else if (hitNode == multiPackage) {
            Toast.makeText(ArActivity.this, "Du hast einen Pakethaufen und somit 3 Pakete eingesammelt", Toast.LENGTH_SHORT).show();

            //Deletes model from view and deletes Anchor
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;

            //Edit Inventory
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 3).apply();

            intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
            startDeliveryTimer();
        } else if (hitNode == wagonPackage) {
            Toast.makeText(ArActivity.this, "Du hast einen Lieferwagen und somit 7 Pakete eingesammelt", Toast.LENGTH_SHORT).show();

            //Deletes model from view and deletes Anchor
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;

            //Edit Inventory
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 7).apply();

            intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
            startDeliveryTimer();
        }
        beforeChange();

        //New activity without transition
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * Creates and starts a timespan to deliver the package
     */
    private void startDeliveryTimer() {

        //Evaluates end time, so timer can run in background
        endTime = System.currentTimeMillis() + timeLeft;

        //Starts new timer
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timerRunning = false;
            }
        }.start();
        timerRunning = true;
    }

    /**
     * Saves timer values before new Activity
     */
    private void beforeChange() {
        SharedPreferences.Editor editor = timers.edit();

        //Edits values, that timer basically runs in background
        editor.putLong("millisLeft", timeLeft);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putLong("endTime", endTime);
        editor.apply();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
