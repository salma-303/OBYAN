package com.example.obyan;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.mediarouter.app.MediaRouteButton;

import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;


public class CastApp extends AppCompatActivity {

    private final SessionManagerListener<CastSession> mSessionManagerListener = new SessionManagerListenerImpl();
    private CastContext mCastContext;
    private SessionManager mSessionManager;
    private CastSession mCastSession;
    private MediaMetadata movieMetadata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_app);

        MediaRouteButton mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);

        mCastContext = CastContext.getSharedInstance(this);
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastSession = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }

    private class SessionManagerListenerImpl implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionStarting(CastSession session) {
        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStartFailed(CastSession session, int error) {
            int castReasonCode = mCastContext.getCastReasonCodeForCastStatusCode(error);
            // Handle error
        }

        @Override
        public void onSessionSuspended(CastSession session, int reason) {
        }

        @Override
        public void onSessionResuming(CastSession session, String sessionId) {
        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(CastSession session, int error) {
        }

        @Override
        public void onSessionEnding(CastSession session) {
        }

        @Override
        public void onSessionEnded(CastSession session, int error) {
            finish();
        }
    }
}
