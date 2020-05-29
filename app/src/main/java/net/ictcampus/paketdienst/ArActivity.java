package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ArActivity extends AppCompatActivity implements Node.OnTapListener {

    private ArFragment arFragment;
    private ModelRenderable mailboxRenderable, singlePackageRenderable, multiPackageRenderable, wagonPackageRenderable, activeRenderable;
    boolean placed = false;
    private int tokens;
    private Node mailbox, singlePackage, multiPackage, wagonPackage;
    private Node activeNode;
    private int id;
    private Intent intent;
    private static ArrayList<MarkerOptions> markerOptionsMailBox = new ArrayList<MarkerOptions>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        //Initialization of all important variables
        intent = new Intent(getApplicationContext(), MapActivity.class);
        Button btnBack = findViewById(R.id.back);
        id = getIntent().getIntExtra("id", 0);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);
        setupModel();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

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
     * On Camera Move, Frame Updates and Methode gets executed
     *
     * @param frameTime
     */
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        if (frame == null) {
            return;
        }

        //Used to Create Model at a certain Point on the Floor automatically
        if (frame.getCamera().getTrackingState() == TrackingState.TRACKING && !placed) {
            Pose pos = frame.getCamera().getPose().compose(Pose.makeTranslation(0, 0, -3f));

            //Anchor Node to fix the Model in place
            Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pos);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            //Chooses which model gets loaded
            switch (id) {
                case 2:
                    activeRenderable = singlePackageRenderable;
                    break;
                case 3:
                    activeRenderable = multiPackageRenderable;
                    break;
                case 4:
                    activeRenderable = wagonPackageRenderable;
                default:
                    activeRenderable = mailboxRenderable;
                    break;
            }

            createModel(anchorNode, activeNode, activeRenderable);
            placed = true;
        }
    }

    /**
     * Sets-up model for rendering
     */
    private void setupModel() {
        //Chooses which model gets prepared
        switch (id) {
            case 2:
                singlePackage = new Node();
                activeNode = singlePackage;

                //Builder for model
                ModelRenderable.builder()
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> singlePackageRenderable = renderable)
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
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> multiPackageRenderable = renderable)
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
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> wagonPackageRenderable = renderable)
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
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> mailboxRenderable = renderable)
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
     * Loads Model into the View
     *
     * @param anchorNode Point, where its fixed
     * @param node       Model
     * @param renderable Renderable part of the model
     */
    private void createModel(AnchorNode anchorNode, Node node, Renderable renderable) {

        //Rotate and shrink model
        node.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
        node.setLocalRotation(Quaternion.multiply(Quaternion.axisAngle(new Vector3(0, 0, 1f), 90f), Quaternion.axisAngle(new Vector3(1f, 0, 0), 30f)));

        //Set Anchor, Renderable, TapListener
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.setOnTapListener(this);
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

        //Get Android File
        SharedPreferences inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = inventoryFile.edit();

        //Statements to decide, which model got hit (Switch was not possible)
        if (hitNode.getRenderable() == mailboxRenderable) {

            //Looks, if there are any Packages collected
            if (inventoryFile.getInt("PACKAGES", 0) != 0) {
                Toast.makeText(ArActivity.this, "Du hast das Paket abgegeben", Toast.LENGTH_SHORT).show();

                //Deletes model from view and deletes Anchor
                arFragment.getArSceneView().getScene().removeChild(hitNode);
                hitNode.setParent(null);
                hitNode = null;

                //Edit Inventory
                editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0) + 10)
                        .apply();
                editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) - 1)
                        .apply();

                intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
            }

            //In case a bug happened
            else {
                Toast.makeText(ArActivity.this, "Du hast kein passendes Paket zum abgeben, hol dir eins bevor du wieder kommst", Toast.LENGTH_SHORT).show();
                intent.putExtra("location", getIntent().getParcelableArrayListExtra("location"));
            }
        } else if (hitNode.getRenderable() == singlePackageRenderable) {
            Toast.makeText(ArActivity.this, "Du hast ein einzelnes Paket aufgesammelt", Toast.LENGTH_SHORT).show();

            //Deletes model from view and deletes Anchor
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;

            //Edit Inventory
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 1)
                    .apply();

            intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
        } else if (hitNode.getRenderable() == multiPackageRenderable) {
            Toast.makeText(ArActivity.this, "Du hast einen Pakethaufen und somit 3 Pakete eingesammelt", Toast.LENGTH_SHORT).show();

            //Deletes model from view and deletes Anchor
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;

            //Edit Inventory
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 3)
                    .apply();

            intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
        } else if (hitNode.getRenderable() == wagonPackageRenderable) {
            Toast.makeText(ArActivity.this, "Du hast einen Lieferwagen und somit 7 Pakete eingesammelt", Toast.LENGTH_SHORT).show();

            //Deletes model from view and deletes Anchor
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;

            //Edit Inventory
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0) + 7)
                    .apply();

            intent.putExtra("locationMailBox", getIntent().getParcelableArrayListExtra("locationMailBox"));
        }

        //New activity without transition
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}

