package org.pdnk.atlassiantest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.pdnk.atlassiantagfinder.AtlassianTaggedStringFactory;
import org.pdnk.atlassiantagfinder.TagFlags;
import org.pdnk.atlassiantagfinder.TaggedString;
import org.pdnk.atlassiantagfinder.TaggedStringFactory;

public class AtlassianTestMain extends AppCompatActivity
{
    static final String prefix = " ";
    static TextView logOutput;
    static ScrollView baseScroll;
    EditText textInput;

    TaggedStringFactory tagFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squaggle_test_main);

        init();

        L("init finished");
    }

    void init()
    {
        tagFactory = new AtlassianTaggedStringFactory();


        tagFactory.registerTag("useraction", "/me", TagFlags.DEL_END_ENDLESS, 0,
                                        TagFlags.FLAG_IGNORE_CASE |
                                        TagFlags.FLAG_ALLOW_NESTED |
                                        TagFlags.FLAG_STRICTLY_FROM_START);

        tagFactory.registerTag("mentions", "@(\\w+)", 1);
        tagFactory.registerTag("emoticons", "(", ")", 15, TagFlags.FLAG_TRIM_SPACES);

        tagFactory.registerURLTag("links", "http://", TagFlags.DEL_END_WHITESPACE,
                                         TagFlags.FLAG_START_INCLUSIVE |
                                         TagFlags.FLAG_IGNORE_CASE |
                                         TagFlags.FLAG_TRIM_SPACES);

        tagFactory.registerURLTag("links", "https://", TagFlags.DEL_END_WHITESPACE, 0);
        tagFactory.registerURLTag("links", "www.", TagFlags.DEL_END_WHITESPACE, 0);


        baseScroll = (ScrollView) findViewById(R.id.scrollView);
        logOutput = (TextView) findViewById(R.id.logOutput);

        textInput = (EditText) findViewById(R.id.textInput);
        textInput.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_NULL && event != null)
                {
                    parseTags(v.getText());
                    v.setText("");

                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.buttonFunc1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onFunc1(v);
            }
        });
        findViewById(R.id.buttonClear).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClearLog(v);
            }
        });

        onClearLog(null);
    }

    void onFunc1(View caller)
    {
        parseTags(textInput.getText());
        textInput.setText("");
    }

    void parseTags(CharSequence inputText)
    {
        if(inputText.length() > 0)
            new ParseStringTagsTask().execute(inputText.toString());
    }


    void onClearLog(View caller)
    {
        logOutput.setText(prefix);

    }

    static void L(String msg)
    {
        logOutput.append(msg + "\n" + prefix);
        logOutput.post(new Runnable()
        {
            @Override
            public void run()
            {
                baseScroll.fullScroll(View.FOCUS_DOWN);
            }
        });

    }

    static void L(CharSequence msg)
    {
        logOutput.append(msg);
        logOutput.append("\n" + prefix);
        logOutput.post(new Runnable()
        {
            @Override
            public void run()
            {
                baseScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    private class ParseStringTagsTask extends AsyncTask<String, Void, TaggedString>
    {
        protected TaggedString doInBackground(String... inputString)
        {
            TaggedString taggedString = null;
            try
            {
                taggedString = tagFactory.createFromString(inputString[0]);
            }
            catch (Exception e)
            {
                L("Error while parsing the original string \"" + inputString[0] + "\"");
            }

            return taggedString;
        }

        protected void onPostExecute(TaggedString result)
        {
            if(result != null)
            {
                L("Original string: " + result.getOriginalString());

                try
                {
                    L(result.toJSON().toString(4));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    L("Error while converting JSON to String");
                }
            }
        }
    }

}
