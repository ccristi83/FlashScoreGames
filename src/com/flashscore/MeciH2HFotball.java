package com.flashscore;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.util.List;

public class MeciH2HFotball {
    private WebDriver driver;
    public String GAME_URL;

    private String replacerH2H = "#h2h;overal";
    private String replacerOdds = "#odds-comparison;1x2-odds;full-time";

    public MeciH2HFotball(WebDriver driver, String URL)
    {
        this.driver = driver;
        this.GAME_URL = URL;
    }

    public MeciH2HFotball(WebDriver driver)
    {
        this.driver = driver;
    }

    public PageGameFotball processGameFotball() throws ParseException, InterruptedException, Exception {
        driver.get(GAME_URL);
        Thread.sleep(3000);

        PageGameFotball game_page = new PageGameFotball();
        String link_odd = GAME_URL.replace(replacerH2H,replacerOdds);

        game_page.setLinkOdds(link_odd);

        List<WebElement> h2hElements = driver.findElements(By.className("h2h-wrapper"));
        WebElement championship = driver.findElement(By.className("description"));
        WebElement teams = driver.findElement(By.className("h2h-submenu"));

        try {
            game_page.processTeams(teams.getText());
        } catch (Exception e)
        {
            System.out.println("Could not process team names; Fail at URL : " + GAME_URL);
        }

        try {
            game_page.processChampionshipString(championship.getText().toString());
        } catch (Exception e)
        {
            System.out.println("Could not process championship name; Fail at URL : " + GAME_URL);
        }

        try {
            game_page.processH2HString(h2hElements);
        } catch (Exception e) {
            System.out.println("Could not process h2h games; Fail at URL : " + GAME_URL);
        }
        return game_page;
    }

    public String[] getOdds(String link_odds) throws InterruptedException {
        driver.get(link_odds);
        Thread.sleep(2000);
        String [] odds1x2 = new String[3];
        try {
            WebElement odds = driver.findElement(By.id("tab-match-odds-comparison"));

            WebElement odds6 = driver.findElement(By.className("odd"));
            odds1x2 = odds6.getText().toString().split("\n");
        } catch (Exception e)
        {
            System.out.println("Could not process odds; Fail at URL : " + GAME_URL);

        }
        //if odd is not present set n/a
        if (odds1x2.length !=3) odds1x2 = new String[]{"n/a", "n/a", "n/a"};

        return odds1x2;
    }

}