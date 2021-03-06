package be.tarsos.tarsos.dsp.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class TarsosDSPActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tarsos_dsp);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
		
		dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {

			@Override
			public void handlePitch(PitchDetectionResult pitchDetectionResult,
									AudioEvent audioEvent) {
				final float pitchInHz = pitchDetectionResult.getPitch();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						TextView text = (TextView) findViewById(R.id.textView1);
						text.setText("" + pitchInHz);
					}
				});

			}
		}));


		dispatcher.addAudioProcessor(new SilenceDetector() {
			float threshold = -60;//dB
			@Override
			public boolean process(AudioEvent audioEvent) {
				final float[] buffer = audioEvent.getFloatBuffer();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						boolean silence = isSilence(buffer, threshold);
					if(silence){
						TextView text = (TextView) findViewById(R.id.textView2);
						text.setText(R.string.silence);
						}
					else{
					TextView text = (TextView) findViewById(R.id.textView2);
					text.setText(R.string.noise);
					}
					}
				});
				return true;
			}
		});
		new Thread(dispatcher,"Audio Dispatcher").start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tarsos_ds, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tarsos_ds,
					container, false);
			return rootView;
		}
	}
}
