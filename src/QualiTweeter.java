import Analysis.CompareUsers;
import Analysis.TextVisualizer;
import Analysis.TweetStatistics;
import Analysis.UserRating;
import Entity.*;
import Utils.BearerToken;
import Utils.WordArrays;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class QualiTweeter {

    public static void main(String[] args) throws IOException{
        
        BearerToken bToken = new BearerToken();
        
//        TextVisualizer tV = new TextVisualizer(new User("kanyewest", bToken));
//        tV.printVisualProperties();
        
//        TweetStatistics tS = new TweetStatistics("./src/DataSources/trainingSet.txt");
//        tS.printStats();
        
        /*
        int count = 0;
        for(int i = 0; i < tS.getSpamWordsList().size()/2; i++){
            if(tS.getNumbersList().get(i) == 0){
                count++;
            }
        }
        System.out.println(count);
        */
        
        User user1 = new User("heynaveed", bToken);
        //User user2 = new User("kanyewest", bToken);
        //System.out.println("Popularity Factors");
        //for(int i = 0; i < WordArrays.RATING_USERNAMES.length; i++){
        //    User user = new User(WordArrays.RATING_USERNAMES[i], bToken);
        //    System.out.println(user.getUserRating().getPopularity());
        //}
        
        //System.out.println(user2.getLastRelevantTweets().size());
        //user2.printTweets(user2.getLastRelevantTweets());
        //user1.printProfileInfo();
        //user2.printProfileInfo();
        //user2.printKeywordMap();
        user1.getWordMapper().printKeywordMap();
       
        //user2.printTweets(user2.getLastRelevantTweets());
        //user2.getWordMapper().getOrderedMapList().printList();
        
        //CompareUsers cU = new CompareUsers(user1, user2);
        //cU.printComparisonResults();
        //cU.printTopWordMatchesList();
        //cU.printCombinedKeywordMap();
        
        /*
        String[] users = {"heynaveed", "loyalbeing", "cdquality",
                            "ashrafislam94", "Apollog94", "libbyhabib", "leonrahigaffar", "_drinkvodka",
                            "khalidxhussein", "mnaatasha", "hakrel", "MackCMDN", "aliali264", "Nick0oh",
                            "ifiwasfat", "kayxde", "emstrudz", "rafqana", "aurameyang", "anamsmalik", 
                            "89_nms", "samimma", "sweetandsalwa", "sanakvmal", "great1pro", "yusufzubi",
                            "mxhid", "onlyt5gives", "spookysaif", "shadd95", "tvrrxll", "saadchowdhary",
                            "iHateFeds", "KarimuDesu", "SonykAzrael", "PhilADill", "nativehumza",
                            "Khun976", "Maaaaheen", "ssjChloe", "FaisalTreShah", "yourservez", "thenameisnihal",
                            "R4H44B", "Teymour_Ashkan", "__sumera", "ThatPrickImran", "Rav_0322", "yourstrulysev",
                            "Amrik_97", "zfi_a", "_khaanvict", "stsiddiqui98", "ghassan223", "JustinSaitta",
                            "hrzvrs", "rmcilroyy", "patel_damini", "DaniAhmed95", "mz_mahmood", "youmanrollthafe",
                            "sudxni", "ramannoodles19", "humairakhan_", "sempiternaIx", "Saattjje", "zeshvn",
                            "LifeofPandya", "SimranOvO", "askatuu", "_Chapati_Head_", "j4vedk", "arabicmami",
                            "ItsMazz", "Farheelmanzoor", "rupikaur_", "Devon_Patel_", "ArabFellaa",
                            "KingShahWxL", "IMoneymusic", "OsamaBinSwagger", "HoldMyDherka", "ribaenizzuo", "Mxnnny",
                            "KINGHash_", "SadWiseThinker", "stevensuvoltos", "ladenbinjalebi", "GuvSanger10",
                            "_adean", "gowsiiii", "SlaveofAlRaheem", "z4hra__", "Nabeel_7_", "Jhovan__", "foundbeau",
                            "imandem_", "sonjatamaraxx", "mohanelh", "GauravHmusic", "BorisSekar", "tedddybuckshot",
                            "hassanishassan", "k_ir_n", "TannedGod", "yee7us", "AzmarKhan", "juds77", "_ksolanki",
                            "kthanksbye__", "Chetan_C07", "Javssxx", "indianbaes", "vxvxk", "____Karan", 
                            "krashxo", "Marshaaaaaall", "_Ahmx", "Tayyxb", "kingleoj_", "Rizzie_", "saad_khan3",
                            "ChimsLDN", "LordSamiie", "ImJunnyy", "SplashKing41", "RashadAhmad95", "uzairjaat",
                            "IDKFAMWTF", "MeloKabir", "lolhvmz", "_vsahota", "ZainsConscience", "es_j5",
                            "enlightenlove_", "vintagerogues", "AJ2k16", "suraaj_d", "SaheraaDesert", 
                            "LurpakShakur", "___xna", "YourAboo", "Donte_ThePoet"};
        
        for(int i = 0; i < users.length; i++){
            User u = new User(users[i], bToken);
            u.printTweets(u.getLastRelevantTweets());
        }
        */
    }
}
