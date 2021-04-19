package com.flashscore;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FlashScore {
    public static final WebDriver driver = new FirefoxDriver();
    public static final String BASE_URL = "https://www.flashscore.com/";
    public static final String BASE_URL_HOCHEY = "https://www.flashscore.com/hockey/";
    public List<WebElement> gamesWebElementsList;
    public List<PageGameFotball> allGamesListFotball = new ArrayList<>();
    public List<PageGameHockey> allGamesListHockey = new ArrayList<>();
    public static String day_Tommorow = "DAY_TOMORROW";
    public static String day_Today = "DAY_TODAY";
    public static String sport_Fotball = "fotball";
    public static String sport_Hockey = "hockey";
    //    public static int max_process_no = 150;
    public static int max_process_no = 11;

    public  int getAllGamesLinks(String day, String base_url) {
        try {
            driver.get(base_url);
            if (day.contains(com.flashscore.FlashScore.day_Tommorow))
            {
                Thread.sleep(2000);
                selectTomorrow();
            };
            Thread.sleep(5000);
            gamesWebElementsList = driver.findElements(By.className("event__match"));
            Thread.sleep(15000);
            System.out.println("+++Found: " + gamesWebElementsList.size() + " games");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gamesWebElementsList.size();
    }

    public List<String> getLinksformWebElements(int count_form, int count_to, String sport) throws InterruptedException {

        List<String> linksURLGames = new ArrayList<String>();
        for (int i = count_form; i <count_to; i++) {
            WebElement meci = gamesWebElementsList.get(i);
            String id = meci.getAttribute("id");
            id = id.substring(id.lastIndexOf("_")+1, id.length());
            String newURl = String.format("https://www.flashscore.com/match/%s/#match-summary",id);
            newURl = newURl.replace("#match-summary", "");
            if (sport == "fotball") newURl = newURl + "#h2h/overall";
            if (sport == "hockey") newURl = newURl + "#h2h/overall";
            linksURLGames.add(newURl);
        }
        return linksURLGames;
    }

    public static void selectTomorrow() {
        WebElement calendar = driver.findElement(By.className("calendar"));
        List<WebElement> days = calendar.findElements(By.className("calendar"));
        calendar.findElements(By.className("calendar__nav")).get(1).click();
    }

    public static List<PageGameFotball> processLinksFotball(List<String> links_in)
    {
        List<PageGameFotball> games_processed = new ArrayList<>();
        links_in.forEach(s ->
        {
            MeciH2HFotball meci = new MeciH2HFotball(driver, s);
            try {
                PageGameFotball processed_game = meci.processGameFotball();
                if (!processed_game.isFilter_out())
                {
                    games_processed.add(processed_game);
                }
            } catch (Exception e) {
                System.out.println("Could not porcess " + meci.GAME_URL.toString());
                e.printStackTrace();
            }
        });
        return games_processed;
    }

    public static List<PageGameHockey> processLinksHockey(List<String> links_in)
    {
        List<PageGameHockey> games_processed = new ArrayList<>();
        links_in.forEach(s ->
        {
            MeciH2HHockey meci = new MeciH2HHockey(driver, s);
            try {
                PageGameHockey processed_game = meci.processGameHockey();
                if (!processed_game.isFilter_out())
                {
                    games_processed.add(processed_game);
                }
            } catch (Exception e) {
                System.out.println("Could not porcess " + meci.GAME_URL.toString());
                e.printStackTrace();
            }
        });
        return games_processed;
    }

    private void processOddsFotball(List<PageGameFotball> list_odds) {

        list_odds.forEach(
                s->
                {
                    MeciH2HFotball meci = new MeciH2HFotball(driver);
                    try {
                        s.setOdds(meci.getOdds(s.getLinkOdds()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        );
    }

    private void processOddsHockey(List<PageGameHockey> list_odds) {

        list_odds.forEach(
                s->
                {
                    MeciH2HHockey meci = new MeciH2HHockey(driver);
                    try {
                        s.setOdds(meci.getOdds(s.getLinkOdds()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        );
    }


    private static StringBuffer printGamesFotball(List<PageGameFotball> games, String msg) {

        StringBuffer tmp = new StringBuffer();
        tmp.append("\n ===" + msg + "===");
        System.out.println("\n  " + msg);

        {for(PageGameFotball g:games)

        {
            //don't print games without odds
            if (!g.getOdds(0).contains("n/a"))
                tmp.append("\n" + g.printGameOdds());
        }}

        tmp.append("\n================================\n\n");
        return tmp;
    }

    private static StringBuffer printGamesHockey(List<PageGameHockey> games, String msg) {

        StringBuffer tmp = new StringBuffer();
        tmp.append("\n ===" + msg + "===");
        System.out.println("\n  " + msg);

        {for(PageGameHockey g:games)

        {
            //don't print games without odds
            if (!g.getOdds(0).contains("n/a"))
                tmp.append("\n" + g.printGameOdds());
        }}

        tmp.append("\n================================\n\n");
        return tmp;
    }

    private static StringBuffer printGamesEquals(List<PageGameFotball> games, String msg, int no_games) {

        StringBuffer tmp = new StringBuffer();
        tmp.append("\n ===" + msg + "===");
        System.out.println("\n  " + msg);

        //if list size bigger than no_games, keep only last no_games
        if(games.size()>=no_games)
        {
            games = games.subList(games.size()-no_games, games.size());
        }

        for(PageGameFotball g:games)
        {
            tmp.append("\n" + g.printGameEqualGames());
        }
        tmp.append("\n================================\n\n");
        return tmp;
    }

    private static StringBuffer printGamesMin(List<PageGameFotball> games, String msg, int no_games) {

        StringBuffer tmp = new StringBuffer();
        tmp.append("\n ===" + msg + "===");
        System.out.println("\n  " + msg);

        //if list size bigger than no_games, keep only last no_games
        if(games.size()>=no_games)
        {
            games = games.subList(0, no_games);
        }

        for(PageGameFotball g:games)
        {
            tmp.append("\n" + g.printGameMinGames());
        }
        tmp.append("\n================================\n\n");
        return tmp;
    }


    public static void main(String[] args) throws InterruptedException, MessagingException, javax.mail.MessagingException {

        System.setProperty("webdriver.firefox.driver", "geckodriver");
        FlashScore page = new FlashScore();

        //select day - default is today
        String day=day_Today;

        if (args[0].contains("today")) day = day_Today;
        if (args[0].contains("tomorrow")) day = day_Tommorow;

        //---FOOTBALL--
        int no_games = page.getAllGamesLinks(day,BASE_URL);
        List<String> all_links = page.getLinksformWebElements(0,no_games, sport_Fotball);

//        List<String> all_links = new ArrayList<>();
//        all_links.add("https://www.flashscore.com/match/Wbg22uqe/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/44ygyKOc/#h2h;overall");
//
//        all_links.add("https://www.flashscore.com/match/AmyRuYsO/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/bFhh0FQp/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/OjyTr5Ra/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/29UFusAE/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/42jw0Hhp/#h2h;overall");
//
//        all_links.add("https://www.flashscore.com/match/Sv2JOGWt/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/8tIYcoy8/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/42jw0Hhp/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/G0Iqozh0/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/KlZ0nuc0/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/lpuz17bf/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/ddBmUx9J/#h2h;overall");
//        all_links.add("https://www.flashscore.com/match/YqLn72AF/#h2h;overal");
//        all_links.add("https://www.flashscore.com/match/fm1YgaAs/#h2h;overal");
//        int no_games = all_links.size();

//        all_links =all_links.subList(0,10);
//        no_games = 10;

        //get chunks of links from of max_process_no
        if (no_games < max_process_no)
        {
            page.allGamesListFotball = processLinksFotball(all_links);
        } else
        {
            int lopp = no_games/max_process_no;
            for (int i = 0; i<lopp; i++) {
                System.out.println("+++Tura: " + (i+1) + " de la: " + i*max_process_no + " pana la : "+ max_process_no*(i+1));
                List<PageGameFotball> games = processLinksFotball(all_links.subList(i*max_process_no,max_process_no*(i+1)));
                page.allGamesListFotball.addAll(games);
            }
            System.out.println("+++Tura ultima de la: " + max_process_no*lopp + " pana la : " + no_games);
            List<PageGameFotball> games = processLinksFotball(all_links.subList(max_process_no*lopp, no_games));
            page.allGamesListFotball.addAll(games);
        }

        //filter games - get the ones having score > 8 and last3Fav > 8
        List<PageGameFotball> lista_ord_fotball_score = page.allGamesListFotball.stream().sorted(Comparator.comparingInt(PageGameFotball::getResultedScore)).filter(o->o.getResultedScore()>8 && o.getFavLast3()>8).collect(Collectors.toList());

        //filter games by goals on last games
        List<PageGameFotball> lista_ord_goals_max = page.allGamesListFotball.stream().sorted(Comparator.comparingInt(PageGameFotball::getScoreGoals)).filter(o->o.getScoreGoals()>23).collect(Collectors.toList());

        //get games by scored goals
        List<PageGameFotball> lista_ord_goals_min = page.allGamesListFotball.stream().sorted(Comparator.comparingInt(PageGameFotball::getScoreGoals)).collect(Collectors.toList());

        //get games - with equals games
        List<PageGameFotball> lista_equals_games = page.allGamesListFotball.stream().sorted(Comparator.comparingInt(PageGameFotball::getEqualGames)).collect(Collectors.toList());

//        System.out.println("\n ++++++ Lista ord: ");
//        lista_ord_fotball_score.forEach(game -> System.out.println(game.getResultedScore() + " " + game.getTeamHome() + " " + game.getTeamAway()));

        //for each game in list, set odds if present
        page.processOddsFotball(lista_ord_fotball_score);


        //---HOCHEY--
        driver.get(BASE_URL);
        Thread.sleep(2000);
        no_games = page.getAllGamesLinks(day,BASE_URL_HOCHEY);
        List<String> all_links_hochey = page.getLinksformWebElements(0,no_games,sport_Hockey);

//        List<String> all_links_hochey = new ArrayList<>();
//        all_links_hochey.add("https://www.flashscore.com/match/fs8APVn3/#h2h;overall");
//        all_links_hochey.add("https://www.flashscore.com/match/Gb3V1Sfd/#h2h;overall");
//        all_links_hochey.add("https://www.flashscore.com/match/CzQUbzbc/#h2h;overall");
//        all_links_hochey.add("https://www.flashscore.com/match/pnktSPP1/#h2h;overall");
//        all_links_hochey.add("https://www.flashscore.com/match/UH7XJp3s/#h2h;overall");
//        all_links_hochey.add("https://www.flashscore.com/match/zXcLIHhO/#h2h/overall");
//        all_links_hochey.add("https://www.flashscore.com/match/fwGrClMS/#h2h/overall");
//        all_links_hochey.add("https://www.flashscore.com/match/MwVW2ZVh/#h2h/overall");

//        int no_games = all_links_hochey.size();

//        all_links_hochey = all_links_hochey.subList(0,3);
//        no_games=3;

        if (no_games < max_process_no)
        {
            page.allGamesListHockey = processLinksHockey(all_links_hochey);
        } else
        {
            int lopp = no_games/max_process_no;
            for (int i = 0; i<lopp; i++) {
                System.out.println("+++Tura: " + (i+1) + " de la: " + i*max_process_no + " pana la : "+ max_process_no*(i+1));
                List<PageGameHockey> games = processLinksHockey(all_links_hochey.subList(i*max_process_no,max_process_no*(i+1)));
                page.allGamesListHockey.addAll(games);
            }
            System.out.println("+++Tura ultima de la: " + max_process_no*lopp + " pana la : " + no_games);
            List<PageGameHockey> games = processLinksHockey(all_links_hochey.subList(max_process_no*lopp, no_games));
            page.allGamesListHockey.addAll(games);
        }

        //filter games - get the ones having score > 8 and last3Fav > 8
        List<PageGameHockey> lista_ord_hochey_score = page.allGamesListHockey.stream().sorted(Comparator.comparingInt(PageGameHockey::getResultedScore)).filter(o->o.getResultedScore()>8 && o.getFavLast3()>8).collect(Collectors.toList());
//        List<PageGameHockey> lista_ord_hochey_score = page.allGamesListHockey.stream().sorted(Comparator.comparingInt(PageGameHockey::getResultedScore)).filter(o->o.getResultedScore()>3).collect(Collectors.toList());

        page.processOddsHockey(lista_ord_hochey_score);

        driver.manage().deleteAllCookies();
        driver.close();



//        System.out.println("\n ++++++ Lista odds: ");
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
//        lista_ord_fotball_score.forEach(game -> System.out.println(dateFormat.format(game.getDate_hour()) + " :: Scor: " + game.getResultedScore() + " :: Last3_fav: " + game.getFavLast3() + " :: "
//                + game.getOdds(0) + " " + game.getOdds(1) + " " + game.getOdds(2) + "\n"
//                + "         " + game.getTeamHome() + "- " + game.getTeamAway() + " :: " + game.getCampionship()));
//
//        System.out.println("\n ++++++ Lista goals: ");
//        lista_ord_goals.forEach(game -> System.out.println(dateFormat.format(game.getDate_hour()) + " :: Goals: " + game.getScoreGoals()+ " :: "
//                + " " + game.getTeamHome() + "- " + game.getTeamAway() + " :: " + game.getCampionship()));


        Mail mail = new Mail();
        mail.sendMail("Flashscore Games " + day,
                String.valueOf(printGamesFotball(lista_ord_fotball_score, String.format("Lista meciuri fotbal: (din total %s)", page.allGamesListFotball.size()))) +
                        String.valueOf(printGamesHockey(lista_ord_hochey_score, String.format("Lista meciuri hockey: (din total %s)", page.allGamesListHockey.size()))) +
                        String.valueOf(printGamesEquals(lista_equals_games, "Lista meciuri egale", 6))+
                        String.valueOf(printGamesMin(lista_ord_goals_min, "Lista meciuri nr goluri min", 10)));


//        mail.sendMail("Flashscore Games " + day, String.valueOf(printGamesHockey(lista_ord_hochey_score, "Lista meciuri hochey: ")));

    }
}