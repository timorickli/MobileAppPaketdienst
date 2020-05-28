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

public class ArActivity extends AppCompatActivity implements Node.OnTapListener {

    private ArFragment arFragment;
    private ModelRenderable mailboxRenderable, singlePackageRenderable, multiPackageRenderable, wagonPackageRenderable, activeRenderable;
    boolean placed = false;
    private int tokens;
    private Node mailbox, singlePackage, multiPackage, wagonPackage;
    private Node activeNode;
    private int id;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        intent = new Intent(getApplicationContext(), MapActivity.class);
        Button btnBack = findViewById(R.id.back);
        id = getIntent().getIntExtra("id",0);


        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        setupModel();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        /*arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                createModel(anchorNode);
            }
        });*/

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null) {
            return;
        }

        if(frame.getCamera().getTrackingState()== TrackingState.TRACKING && !placed) {
            Pose pos = frame.getCamera().getPose().compose(Pose.makeTranslation(0 , 0, -3f));
            Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pos);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            switch (id){
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


    private void setupModel() {
        switch (id){
            case 2:
                multiPackage = new Node();
                activeNode = singlePackage;

                ModelRenderable.builder()
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> singlePackageRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ",Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;

            case 3:
                multiPackage = new Node();
                activeNode = multiPackage;

                ModelRenderable.builder()
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> multiPackageRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ",Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;

            case 4:
                wagonPackage = new Node();
                activeNode = wagonPackage;

                ModelRenderable.builder()
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> wagonPackageRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ",Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;

            default:
                mailbox = new Node();
                activeNode = mailbox;

                ModelRenderable.builder()
                        .setSource(this, R.raw.mailbox)
                        .build().thenAccept(renderable -> mailboxRenderable = renderable)
                        .exceptionally(
                                throwable -> {
                                    Toast.makeText(this, "Unable to show ",Toast.LENGTH_SHORT).show();
                                    return null;
                                }
                        );
                break;
        }
    }

    private void createModel(AnchorNode anchorNode, Node node, Renderable renderable) {
        node.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
        node.setLocalRotation(Quaternion.multiply(Quaternion.axisAngle(new Vector3(0,0,1f), 90f), Quaternion.axisAngle(new Vector3(1f,0,0),30f)));
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.setOnTapListener(this);
    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        Node hitNode = hitTestResult.getNode();
        SharedPreferences inventoryFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= inventoryFile.edit();

        if (hitNode.getRenderable() == mailboxRenderable) {
            if (inventoryFile.getInt("PACKAGES", 0) != 0){
                Toast.makeText(ArActivity.this, "Du hast das Paket abgegeben", Toast.LENGTH_SHORT).show();
                arFragment.getArSceneView().getScene().removeChild(hitNode);
                hitNode.setParent(null);
                hitNode = null;

                editor.putInt("TOKENS", inventoryFile.getInt("TOKENS", 0)+10)
                        .apply();
                editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0)-1)
                        .apply();
                intent.putExtra("status", true);

            }else{
                Toast.makeText(ArActivity.this, "Du hast kein passendes Paket zum abgeben, hol dir eins bevor du wieder kommst", Toast.LENGTH_SHORT).show();
            }

        }
        else if (hitNode.getRenderable() == singlePackageRenderable){
            Toast.makeText(ArActivity.this, "Du hast das Paket aufgesammelt", Toast.LENGTH_SHORT).show();
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0)+1)
                    .apply();
            intent.putExtra("status", true);
        }
        else if (hitNode.getRenderable() == multiPackageRenderable){
            Toast.makeText(ArActivity.this, "Du hast das Paket aufgesammelt", Toast.LENGTH_SHORT).show();
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0)+3)
                    .apply();
            intent.putExtra("status", true);
        }else if (hitNode.getRenderable() == wagonPackageRenderable){
            Toast.makeText(ArActivity.this, "Du hast das Paket aufgesammelt", Toast.LENGTH_SHORT).show();
            arFragment.getArSceneView().getScene().removeChild(hitNode);
            hitNode.setParent(null);
            hitNode = null;
            editor.putInt("PACKAGES", inventoryFile.getInt("PACKAGES", 0)+7)
                    .apply();
            intent.putExtra("status", true);
        }
    }
}

