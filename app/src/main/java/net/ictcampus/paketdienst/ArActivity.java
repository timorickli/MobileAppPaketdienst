package net.ictcampus.paketdienst;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
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
import com.google.ar.sceneform.ux.ArFragment;

public class ArActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable mailboxRenderable;
    boolean placed = false;
    private int tokens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

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

        SharedPreferences tokensFile = this.getSharedPreferences("inventory", Context.MODE_PRIVATE);
        tokens = tokensFile.getInt("TOKENS", 0);
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
            createModel(anchorNode);
            placed = true;
        }
    }


    private void setupModel() {
        ModelRenderable.builder()
                .setSource(this, R.raw.mailbox)
                .build().thenAccept(renderable -> mailboxRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to show ",Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
    }

    private void createModel(AnchorNode anchorNode) {
        Node mailbox = new Node();
        mailbox.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
        mailbox.setLocalRotation(Quaternion.multiply(Quaternion.axisAngle(new Vector3(0,0,1f), 90f), Quaternion.axisAngle(new Vector3(1f,0,0),30f)));
        mailbox.setParent(anchorNode);
        mailbox.setRenderable(mailboxRenderable);
        mailbox.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                tokens = tokens + 10;
                SharedPreferences tokenFile = getSharedPreferences("inventory", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= tokenFile.edit();
                editor.putInt("TOKENS", tokens)
                        .apply();

                Intent intent= new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
