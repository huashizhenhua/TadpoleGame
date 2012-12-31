package org.tadpole.app;

import org.tadpole.service.TestService;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class PluginActivity extends Activity {

    private Button btnNotifyCaller = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        btnNotifyCaller = (Button) this.findViewById(R.id.notifyCaller);
        btnNotifyCaller.setOnClickListener(new View.OnClickListener() {
            private int i = 0;
            @Override
            public void onClick(View v) {
                TestService.notify("msg = " + (++i));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
