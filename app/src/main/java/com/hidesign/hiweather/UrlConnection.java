package com.hidesign.hiweather;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

class UrlConnection {
    private HttpURLConnection urlConnect = null;
    private BufferedReader bufferedReader = null;
    private String apiKey = "aDv8fsGqxTBQ0zmXKfqxLA53uuCnJK4Z";

    String Url(String url) {
        {
            try {
                URL connect = new URL(url);
                try {
                    urlConnect = (HttpURLConnection) connect.openConnection();
                    urlConnect.connect();

                    InputStream is = urlConnect.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder buffer = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    return buffer.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } finally {
                if (urlConnect != null) {
                    urlConnect.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return url;
    }

    String CurrentConditions(String key) {
        String currentconditions = "https://dataservice.accuweather.com/currentconditions/v1/" + key + "?apikey="+ apiKey +"&details=true";
        StringBuffer bufferForecast = null;
        try {
            URL connect = new URL(currentconditions);
            try {
                urlConnect = (HttpURLConnection) connect.openConnection();
                urlConnect.connect();

                InputStream is = urlConnect.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line;
                bufferForecast = new StringBuffer();

                while ((line = bufferedReader.readLine()) != null) {
                    bufferForecast.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        assert bufferForecast != null;
        return bufferForecast.toString();
    }

    String forecast (String key) {
        String forecastConditions = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/" + key + "?apikey="+ apiKey +"&details=true&metric=true";
        StringBuffer bufferForecast = null;
        try {
            URL connect = new URL(forecastConditions);
            try {
                urlConnect = (HttpURLConnection) connect.openConnection();
                urlConnect.connect();

                InputStream is = urlConnect.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line;
                bufferForecast = new StringBuffer();

                while ((line = bufferedReader.readLine()) != null) {
                    bufferForecast.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        assert bufferForecast != null;
        return bufferForecast.toString();
    }
    ArrayList<String> search (String city){
        ArrayList<String> results = new ArrayList<>();
        String currentconditions = "http://dataservice.accuweather.com/locations/v1/cities/autocomplete?apikey="+ apiKey +"&q=";
        Log.e("URLCONNECT", "search: " + currentconditions );
        try {
            URL connect = new URL(currentconditions);
            try {
                urlConnect = (HttpURLConnection) connect.openConnection();
                urlConnect.connect();

                InputStream is = urlConnect.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    results.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (urlConnect != null) {
                urlConnect.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return results;
    }
}