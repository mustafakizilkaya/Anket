package com.LH.androidtcp.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.Hashtable;


public class MainActivity extends Activity {

    private TCPClient mTcpClient;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // servera bağlan
        new connectTask().execute("");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mTcpClient != null) {
            mTcpClient.sendMessage("komut=anketIstegi");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void createSurvey(final String[] questionsAndPlaces) {

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        final String[] onlyQuestions = new String[questionsAndPlaces.length];
        final String[] onlyPlaces = new String[questionsAndPlaces.length];

        for (int i = 0; i < questionsAndPlaces.length; i++) {
            String[] questionAndPlacesSide = questionsAndPlaces[i].split("\\-");

            onlyQuestions[i] = questionAndPlacesSide[0];
            onlyPlaces[i] = questionAndPlacesSide[1];

            TableRow tr = new TableRow(this);

            // Text view ekle
            TableRow.LayoutParams layoutTableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);

            tr.setLayoutParams(layoutTableRowParams);

            TableRow.LayoutParams layoutTextViewParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);

            layoutTextViewParams.setMargins(10, 0, 0, 0);

            TextView textQuestionView = new TextView(this);
            textQuestionView.setTextAppearance(this, android.R.style.TextAppearance_Large);
            textQuestionView.setText(i + " - " + questionAndPlacesSide[0]);
            textQuestionView.setSingleLine(false);
            textQuestionView.setMaxLines(3);
            textQuestionView.setGravity(Gravity.LEFT | Gravity.CENTER);
            textQuestionView.setLines(3);
            textQuestionView.setLayoutParams(layoutTextViewParams);
            tr.addView(textQuestionView);

            if (Integer.parseInt(questionAndPlacesSide[1]) < 16) // seçmeli soru rating bar ve text
            {
                TableRow.LayoutParams layoutRatingBarParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                layoutRatingBarParams.setMargins(0, 0, 10, 0);

                // Rating Bar ekle
                RatingBar ratingBarView = new RatingBar(this);
                ratingBarView.setTag("" + questionAndPlacesSide[1]);
                ratingBarView.setNumStars(5);
                ratingBarView.setStepSize(1);
                ratingBarView.setRating(1);
                layoutRatingBarParams.gravity = (Gravity.RIGHT | Gravity.CENTER);
                ratingBarView.setLayoutParams(layoutRatingBarParams);
                tr.addView(ratingBarView);
                tableLayout.addView(tr);
            } else // yazılı soru text ve cevap text
            {
                textQuestionView.setLines(1);
                tableLayout.addView(tr);

                TableRow.LayoutParams layoutEditTextParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
                layoutEditTextParams.setMargins(10, 10, 10, 0);

                EditText editText = new EditText(this);
                editText.setSingleLine(false);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                editText.setMinLines(8);
                editText.setLines(8);
                editText.setGravity(Gravity.TOP | Gravity.LEFT);
                editText.setBackgroundResource(R.drawable.buttonstyle);
                editText.setLayoutParams(layoutEditTextParams);
                editText.setTag("" + questionAndPlacesSide[1]);

                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(500);
                editText.setFilters(filters);

                TableRow tr2 = new TableRow(this);

                // Text view ekle
                TableRow.LayoutParams layoutTableRowParams2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);

                tr2.setLayoutParams(layoutTableRowParams2);
                tr2.addView(editText);
                tableLayout.addView(tr2);
            }
        }

        TableRow tr = new TableRow(this);

        TableRow.LayoutParams layoutTableRowParams2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1);

        tr.setLayoutParams(layoutTableRowParams2);

        for (int i = 0; i < 2; i++) {
            TableRow.LayoutParams layoutEditTextParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
            layoutEditTextParams.setMargins(10, 10, 10, 10);

            EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            editText.setLayoutParams(layoutEditTextParams);

            if (i == 0)
                editText.setHint("Ad *");
            else
                editText.setHint("Soyad *");

            editText.setTag("" + (i + 20));

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(63);
            editText.setFilters(filters);
            tr.addView(editText);
        }
        tableLayout.addView(tr, layoutTableRowParams2);


        TableRow.LayoutParams layoutEditTextParams3 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        layoutEditTextParams3.setMargins(10, 0, 10, 10);

        EditText editText3 = new EditText(this);
        editText3.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText3.setLayoutParams(layoutEditTextParams3);

        editText3.setTag("" + 22);

        editText3.setHint("E-posta");

        InputFilter[] filters2 = new InputFilter[1];
        filters2[0] = new InputFilter.LengthFilter(127);
        editText3.setFilters(filters2);

        EditText editText4 = new EditText(this);
        editText4.setInputType(InputType.TYPE_CLASS_PHONE);
        editText4.setLayoutParams(layoutEditTextParams3);
        editText4.setHint("Telefon");

        editText4.setTag("" + 23);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(63);
        editText4.setFilters(filters);

        TableRow tr2 = new TableRow(this);

        tr2.setLayoutParams(layoutTableRowParams2);
        tr2.addView(editText3);
        tr2.addView(editText4);
        tableLayout.addView(tr2);

        TableRow.LayoutParams layoutButtonParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        layoutButtonParams.setMargins(200, 0, 200, 15);

        TableRow tr3 = new TableRow(this);

        // Rating Bar ekle
        Button sendButton = new Button(this);
        sendButton.setText("Anketi Tamamladım");
        sendButton.setLayoutParams(layoutButtonParams);
        sendButton.setBackgroundResource(R.drawable.buttonstyle);
        sendButton.setHeight(75);

        tr3.setLayoutParams(layoutTableRowParams2);
        tr3.addView(sendButton);
        tableLayout.addView(tr3);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder userInfo = new StringBuilder();
                StringBuilder surveyAnswers = new StringBuilder();
                StringBuilder surveyQuestions = new StringBuilder();

                EditText name = (EditText) tableLayout.findViewWithTag("20");
                EditText surname = (EditText) tableLayout.findViewWithTag("21");
                EditText email = (EditText) tableLayout.findViewWithTag("22");
                EditText phone = (EditText) tableLayout.findViewWithTag("23");


                userInfo.append(name.getText() + "*" + surname.getText() + "*" + email.getText() + "*" + phone.getText());

                for (int i = 0; i < onlyQuestions.length; i++) {
                    if (Integer.parseInt(onlyPlaces[i]) < 16)
                        surveyAnswers.append(("*" + ((RatingBar) tableLayout.findViewWithTag(onlyPlaces[i])).getRating()).replace('.',','));
                    else
                        surveyAnswers.append("*" + ((EditText) tableLayout.findViewWithTag(onlyPlaces[i])).getText());
                    surveyQuestions.append("*" + onlyQuestions[i]);
                }

                if (surveyAnswers.length() >= 1) {
                    surveyAnswers.deleteCharAt(0);
                }

                if (surveyQuestions.length() >= 1) {
                    surveyQuestions.deleteCharAt(0);
                }

                if (mTcpClient != null) {
                    mTcpClient.sendMessage("komut=anketCevaplari&kullaniciBilgileri=" + userInfo.toString() + "&cevapBilgileri=" + surveyAnswers.toString() + "&soruBilgileri=" + surveyQuestions.toString());
                }
                AlertDialog.Builder surveyMessage = new AlertDialog.Builder(context);
                surveyMessage.setTitle("Anket Tamamlandı");
                surveyMessage.setMessage("Anketimize katıldığınız için teşekkür ederiz. Yeniden bekleriz..").setCancelable(false).setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = surveyMessage.create();
                alertDialog.show();
            }
        });
    }

    public class connectTask extends AsyncTask<String, String, TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                public void messageReceived(String message) {
                    //mesaj geldi burda işle
                    String[] parameters = message.split("&");
                    String[] sides;

                    final Dictionary<String, String> collection = new Hashtable<String, String>(parameters.length);
                    for (String parameter : parameters) {
                        sides = parameter.split("=");
                        if (sides.length == 2)
                            collection.put(sides[0], sides[1]);
                    }

                    if(collection.get("komut").contentEquals("anketIstegi")) { // anket isteği mesajına gelen cevap
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] questionsAndPlaces = collection.get("sorular").split("\\*");
                                createSurvey(questionsAndPlaces);
                            }
                        });
                    }
                    else // diğer mesajlar hata vs.
                    {

                    }
                }
            });
            mTcpClient.run();

            return null;
        }
    }
}


