/**
 * @author Derek Garcia
 **/

public class Data {

    public enum Language {
        EN,
        NL
    }

    private Language trainingLang;
    private final String fragment;


    public Data(String fragment) throws Exception {

        String[] components = fragment.split("\\|");

        if(components.length == 2){
            switch (components[0]){
                case "en" -> this.trainingLang = Language.EN;
                case "nl" -> this.trainingLang = Language.NL;
                default -> throw new Exception("Unknown language, expected \"en\" or \"nl\" but got \"" + components[0] +"\"");
            }
            this.fragment = components[1];
        } else {
            this.fragment = fragment;
        }
    }

    @Override
    public String toString() {
        return this.fragment;
    }

}
