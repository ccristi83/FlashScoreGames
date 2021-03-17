package com.flashscore;

import org.openqa.selenium.WebElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PageGameFotball implements PageGame{

    private String campionship;
    private Date date_hour;
    private String teamHome;
    private String teamAway;
    private String linkOdds;
    private String[] odds;
    private int scoreGoals;
    private int equalGames =0;

    private int score_teamHome;
    private int score_teamAway;
    private int resultedScore;
    private int favLast3;

    private boolean filter_out;

    private int LAST_MATCHES_TEAM_HOME = 0;
    private int LAST_MATCHES_TEAM_AWAY = 1;
    private int HAEAD_TOHEAD_MATCHES= 2;

    public boolean isFilter_out() {
        return filter_out;
    }

    private void setFilter_out(boolean filter_out) {
        this.filter_out = filter_out;
    }

    public int getFavLast3() {
        return favLast3;
    }

    private void setFavLast3(int favLast3) {
        this.favLast3 = favLast3;
    }

    public int getResultedScore() {
        return resultedScore;
    }

    private void setResultedScore(int resultedScore) {
        this.resultedScore = resultedScore;
    }

    public int getScore_teamHome() {
        return score_teamHome;
    }

    private void setScore_teamHome(int score_teamHome) {
        this.score_teamHome = score_teamHome;
    }

    public int getScore_teamAway() {
        return score_teamAway;
    }

    private void setScore_teamAway(int score_teamAway) {
        this.score_teamAway = score_teamAway;
    }

    public String getLinkOdds() {
        return linkOdds;
    }

    public void setLinkOdds(String linkOdds) {
        this.linkOdds = linkOdds;
    }

    public Date getDate_hour() {
        return date_hour;
    }

    private void setDate_hour(Date date_hour) {
        this.date_hour = date_hour;
    }

    public String getCampionship() {
        return campionship;
    }

    private void setCampionship(String campionship) {
        this.campionship = campionship;
    }

    public String getTeamHome() {
        return teamHome;
    }

    private void setTeamHome(String teamHome) {
        this.teamHome = teamHome;
    }

    public String getTeamAway() {
        return teamAway;
    }

    private void setTeamAway(String teamAway) {
        this.teamAway = teamAway;
    }

    public String getOdds( int i) {
        return odds[i];
    }

    public void setOdds(String[] odds) {
        this.odds = odds;
    }

    public int getScoreGoals() {
        return scoreGoals;
    }

    private void setScoreGoals(int scoreGoals) {
        this.scoreGoals = scoreGoals;
    }

    private void increaseEqualGames() {
        this.equalGames = equalGames+1;
    }

    public int getEqualGames() {
        return equalGames;
    }


    public void processTeams(String teams)
    {
        String [] text = teams.split("\n");
        //this.setTeamHome(text[1]);
        //this.setTeamAway(text[2]);
        this.setTeamHome(text[1].replace("- Home", ""));
        this.setTeamAway(text[2].replace("- Away", ""));
    }
    public void processChampionshipString(String champString) throws ParseException {
        String[] ev = champString.split("\n");
        if (ev[0].contains("Round")) {ev[0] = ev[0].substring(0, ev[0].indexOf("Round")-2);}
        this.setCampionship(ev[0]);
        // filter out Women championship
        if (ev[0].toString().contains("Women")) {setFilter_out(true);}
        this.setDate_hour(getDateHourfromString(ev[1]));
    }

    private Date getDateHourfromString(String dayhourString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        Date day_hour = dateFormat.parse(dayhourString);
        return day_hour;
    }

    public void processH2HString (List<WebElement> h2hString)
    {
        String homeTeamGames = h2hString.get(LAST_MATCHES_TEAM_HOME).getText();
        String awayTeamGames = h2hString.get(LAST_MATCHES_TEAM_AWAY).getText();
        String h2hTeamGames = h2hString.get(HAEAD_TOHEAD_MATCHES).getText();


        if (homeTeamGames.contains("No match found")||awayTeamGames.contains("No match found")) {
            setScore_teamHome(0);
            setScore_teamAway(0);
            setResultedScore(0); }
        else
        {
            //process home team games
            List<String> h2h_home = Arrays.asList(homeTeamGames.split("\n"));
            List<String> h2h_away = Arrays.asList(awayTeamGames.split("\n"));
            List<String> h2h_h2h = Arrays.asList(h2hTeamGames.split("\n"));

            // filter out if no head to head game found
            if (h2h_h2h.get(1).contains("No match found.")) setFilter_out(true);

            setScore_teamHome(computeH2H(h2h_home));
            setScore_teamAway(computeH2H(h2h_away));

            // compute last 3 matches (favorite team)
            if (getScore_teamHome()>getScore_teamAway())
            {
                setFavLast3(computelast3(h2h_home));
            }
            else
            {
                setFavLast3(computelast3(h2h_away));
            }
            setResultedScore(computeResultedScore());
        }
        //if the game wasn't filtered out, compute no of goals
        if (!filter_out)
        {
            System.out.println("home");
            int goalsHome = getGoalsNo(homeTeamGames, 3);

            System.out.println("away");
            int goalAway = getGoalsNo(awayTeamGames, 3);

            System.out.println("h2h");
            int goalsH2h = getGoalsNo(h2hTeamGames, 1);
            setScoreGoals(goalsHome+goalAway+goalsH2h);
            System.out.println("+++scored goals"+getScoreGoals());
        }
    }

    private int computeH2H(List<String> games)
    {
        int result = 0;
        List<String> g = games.stream().filter(e -> e.length()==1).collect(Collectors.toList());
        for (String el:g)
        {
            if (el.toString().equals("W")) result+=3;
            if (el.toString().equals("L")) result+=0;
            if (el.toString().equals("D")) result+=1;
        }
        //if history is no more than 3 games filter out the game
        if (g.size()<3)
            setFilter_out(true);
        return result;
    }

    private int computelast3(List<String> games)
    {
        int result = 0;
        List<String> g = games.stream().filter(e -> e.length()==1).collect(Collectors.toList());
        if (g.size() > 3)
            g = g.subList(0,3);
        for (String el : g) {
            if (el.toString().equals("W")) result += 3;
            if (el.toString().equals("D")) result += 1;
            if (el.toString().equals("L")) result += 0;
        }
        return result;
    }

    private int computeResultedScore()
    {
        int result = Math.abs(this.getScore_teamHome()-this.getScore_teamAway());
        return result;
    }

    private int getGoalsNo(String gameDetails, int no_matches)
    {
        int noGoals = 0;
        String [] goals = gameDetails.split("\n");
        //remove doble line result (especially for hochey)
        List<String> removed_double_result = Arrays.asList(goals);
        removed_double_result = removed_double_result.stream().filter(o -> o.length() > 7).collect(Collectors.toList());

        if (removed_double_result.size()>no_matches+1)
        {
            String lastChars;
            for (int i=1; i<removed_double_result.size()-1; i++) {
                lastChars = removed_double_result.get(i).substring(removed_double_result.get(i).length() -6, removed_double_result.get(i).length()).trim();
                System.out.println(lastChars);
                String[] score = lastChars.split(":");
                noGoals = noGoals + Integer.valueOf(score[0].toString().trim());
                noGoals = noGoals + Integer.valueOf(score[1].toString().trim());
                // if the game score is equal
                if (score[0].toString().trim().equals(score[1].toString().trim())) increaseEqualGames();
            }
        }
        return noGoals;
    }

    private int getGoalsNoH2H(String gameDetails)
    {
        int noGoals = 0;
        String [] goals = gameDetails.split("\n");
        //remove doble line result (especially for hochey)
        List<String> removed_double_result = Arrays.asList(goals);
        removed_double_result= removed_double_result.stream().filter(o -> o.length()>7).collect(Collectors.toList());

        if (goals.length>4)
        {
            String lastChars;
            for (int i=1; i<4; i++) {
                if (goals[i].length()>7) {
                    lastChars = removed_double_result.get(i).substring(goals[i].length() - 5, goals[i].length());
                    System.out.println(lastChars);
                    String[] score = lastChars.split(":");
                    noGoals = noGoals + Integer.valueOf(score[0].toString().trim());
                    noGoals = noGoals + Integer.valueOf(score[1].toString().trim());
                }
            }
        }
        return noGoals;
    }

    public String printGameOdds()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String printed_game = dateFormat.format(getDate_hour()) + " :: Scor: " + getResultedScore() + " :: Last3_fav: " + getFavLast3()
                + "\n              " + getOdds(0) + " " + getOdds(1) + " " + getOdds(2)
                + "\n              " + getTeamHome() + "- " + getTeamAway() + " :: " + getCampionship();
        System.out.println(printed_game);
        return printed_game;
    }

    public String printGameGoals()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String printed_game = dateFormat.format(getDate_hour()) + " :: Goals: " + getScoreGoals()+ " :: "
                + " " + getTeamHome() + "- " + getTeamAway() + " :: " + getCampionship();
        System.out.println(printed_game);
        return printed_game;
    }
}
