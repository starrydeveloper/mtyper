package com.starrydeveloper.mtyper;

import android.*;
import android.content.pm.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import edu.cmu.pocketsphinx.*;
import java.io.*;
import java.lang.ref.*;
import java.util.*;

import static android.widget.Toast.makeText;

public class MyanmarVoiceActivity extends AppCompatActivity implements RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String WORD_SEARCH = "any word";
    private static final String HOW_TO_TEXT = "နားစြင့္ေနပါတယ္ ...\n\nျမန္မာ သင္ပုန္းၾကီး \"က၊ ခ၊\" ကေန ... \"အ\" အထိ ၾကိဳက္ရာကို တစ္လံုးခ်င္း ရြတ္ၾကည္႔ပါ။ ေနာက္ကေန ၾကိဳးစား နားေထာင္ျပီး လိုက္ေရး ၾကည္႔ပါမယ္။ အမွားပါရင္ ခြင့္လႊတ္ပါ။:)";
	
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;

	private TextView hintTv;
	private TextView outputTv;
	private RecognitionProgressView mRecognitionProgressView;
	private ScrollView scrollView;
	private StringBuilder outputTextBuilder = new StringBuilder();
	
	private static AutoMyanmarFontDetector mFontDetector;
	
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.main);
		
		View layout = findViewById(R.id.anyName);
		mFontDetector = new AutoMyanmarFontDetector(this,layout);
		
        outputTv = (TextView) findViewById(R.id.outputTv);
		hintTv = (TextView) findViewById(R.id.hintTv);
		tellHint("မဂၤလာပါ ...\n\nျမန္မာစာလံုးမ်ားကို ပါးစပ္က ေျပာရံုျဖင့္ ေနာက္မွ စာလိုက္ေရးေပးႏိုင္မည္ ျဖစ္ေသာ m-Typer ကို ေရးသားေနဆဲ ျဖစ္ပါသည္။");
		scrollView = (ScrollView) findViewById(R.id.scroll);
		mRecognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognitionProgressView);
	
	  // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
		//new BackgroundSetupTask(this).execute();
	
	  
	} //OnCreate();

    private class BackgroundSetupTask extends AsyncTask<Void, Void, Exception> {
		
        WeakReference<MyanmarVoiceActivity> activityReference;
		
		//Constructor
        public BackgroundSetupTask(MyanmarVoiceActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }
		
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception exception) {
            if (exception != null) {
					reportErrorWhy("ERROR: " + exception);
            } else {
                activityReference.get().listen();
				activityReference.get().tellHint(HOW_TO_TEXT);
				activityReference.get().startWavyProgress();
            }
        }
    } // BackgroundSetUp
	

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // new BackgroundSetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
		}
		listen();
    }
	
    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
       
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            typeOutput(text);
        }
    }
	
    @Override
    public void onBeginningOfSpeech() {
		
    }

    @Override
    public void onEndOfSpeech() {
        
    }
	@Override
    public void onError(Exception error) {
        reportErrorWhy(error.getMessage());
    }

    @Override
    public void onTimeout() {
		listen();
    }

    private void listen() {
		recognizer.stop();
       	recognizer.startListening(WORD_SEARCH, 10000);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
			.setAcousticModel(new File(assetsDir, "en-us-ptm"))
			.setDictionary(new File(assetsDir,"myanmar.dict"))
			// Use context-independent phonetic search, context-dependent is too slow for mobile
			.setBoolean("-allphone_ci", true) 
			.getRecognizer();
			
        recognizer.addListener(this);
		
        // Create grammar-based search for word recognition
        File wordsGrammar = new File(assetsDir, "words.gram");
        recognizer.addGrammarSearch(WORD_SEARCH, wordsGrammar);
    }
	
	private void startWavyProgress() {
		mRecognitionProgressView.setColors(Extensions.PV_COLORS);
		mRecognitionProgressView.setBarMaxHeightsInDp(Extensions.PV_BARS_HEIGHT);
		mRecognitionProgressView.setCircleRadiusInDp(Extensions.PV_CIRCLE_RADIUS);
		mRecognitionProgressView.setSpacingInDp(Extensions.PV_CIRCLE_SPACING);
		mRecognitionProgressView.setIdleStateAmplitudeInDp(Extensions.PV_IDLE_STATE);
		mRecognitionProgressView.setRotationRadiusInDp(Extensions.PV_ROTATION_RADIUS);
		mRecognitionProgressView.rmsValue(205.0f);
		mRecognitionProgressView.play();
		mRecognitionProgressView.invalidate();
	}
	
	private boolean pointer;
	private boolean once;
	
	private void typeOutput(String output) {
		outputTextBuilder.append(output);
		outputTv.setText(mFontDetector.writeZawgyi(outputTextBuilder.toString()));
		scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		outputTextBuilder.append(" ၊ ");
		if(! once)
			fakePointer();
			once = true;
	}
	private void reportErrorWhy(String error) {
		hintTv.setText(error);
	}
	
	private void tellHint(String hint) {
		hintTv.setText(mFontDetector.writeZawgyi(hint));
	}

	private void fakePointer() {
		String text = outputTv.getText().toString().replace(" _","").trim();
		if(! text.isEmpty() && text.length() > 0) {
			if(pointer) {
				outputTv.setText(text);
			} else {
				outputTv.setText(text + " _");
			}
			pointer = ! pointer;
		}
		new Handler().postDelayed((Runnable ) new Runnable() {
									  @Override
									  public void run() {
										  fakePointer();
									  }
								  }, 575);
	}
	
	private void shutAllDown() {
		if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        shutAllDown();
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		shutAllDown();
	}
	
} //Main
