package com.flashscore;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MeciH2HHockey implements MeciH2H{

    private WebDriver driver;
    public String GAME_URL;

    private String replacerH2H = "#h2h/overall";
    private String replacerOdds = "#odds-comparison/1x2-odds/full-time";

    public MeciH2HHockey(WebDriver driver, String URL)
    {
        this.driver = driver;
        this.GAME_URL = URL;
    }

    public MeciH2HHockey(WebDriver driver)
    {
        this.driver = driver;
    }

    public PageGameHockey processGameHockey() throws ParseException, InterruptedException, Exception {
        driver.get(GAME_URL);
        Thread.sleep(3000);

        PageGameHockey game_page = new PageGameHockey();
        game_page.setLinkOdds(GAME_URL.replace(replacerH2H,replacerOdds));
        List<WebElement> h2hElements = driver.findElements(By.className("section___1a1N7yN"));
        WebElement championship = driver.findElement(By.className("country___24Qe-aj"));
        WebElement championship_hour = driver.findElement(By.className("time___FaD-OOU"));
        String date ;
        if (championship_hour.getText().toString().equals(""))
        date = "11.01.2021 00:00";
        else
            date = championship_hour.getText().toString();
        String ch = championship.getText().toString() + "\n" + date;
        WebElement teams = driver.findElement(By.className("subTabs"));


        try {
            game_page.processTeams(teams.getText());
        } catch (Exception e)
        {
            System.out.println("Could not process team names");
        }

        try {
            game_page.processChampionshipString(ch);
        } catch (Exception e)
        {
            System.out.println("Could not process championship name");
        }

        try {
            game_page.processH2HStringTest(h2hElements);
        } catch (Exception e) {
            System.out.println("Could not process h2h games; Fail at URL : " + GAME_URL);
        }
        return game_page;
    }

    public List getOdds(String link_odds) throws InterruptedException {
        driver.get(link_odds);
        Thread.sleep(1000);
        driver.get(link_odds);
        Thread.sleep(2000);
        System.out.println("Processing odds for " + link_odds);
        WebElement row;
        List<String> odds1x2 = Arrays.asList(new String[]{"n/a", "n/a", "n/a"});

        try {
            List<WebElement> odds_rows = driver.findElements(By.className("rows___1BdItrT"));
            Thread.sleep(1000);
            row = odds_rows.get(0);
            if (row.getText().split("\n").length >=3)
                odds1x2 = Arrays.stream(row.getText().split("\n")).limit(3).collect(Collectors.toList());
            System.out.println("Odds: " + odds1x2.toString() + " for " + link_odds);
        } catch (Exception e)
        {
            System.out.println("Could not process odds for " + link_odds);
        }
        return odds1x2;
    }

}

