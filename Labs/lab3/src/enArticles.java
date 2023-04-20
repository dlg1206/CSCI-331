import java.util.ArrayList;
import java.util.List;

/**
 * Check for English articles
 * Not Found:   nl
 * Found:       en
 */
public class enArticles extends Feature {
    private final List<String> articles = new ArrayList<>() {
        {
            add("the");
            add("an");
            add("a");
        }
    };

    @Override
    protected boolean isEnglish(Data data) {
        for (String word : this.articles)
            if (data.containsWord(word))
                return true;
        return false;
    }

    @Override
    protected String getTestName() {
        return "English Articles";
    }
}
