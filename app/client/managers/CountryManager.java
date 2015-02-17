package client.managers;

import java.util.List;
import java.util.ArrayList;

import rpc.client.ClientRequest;

import models.Country;

import client.services.WBIExplorationService;

public class CountryManager implements Manager {
    public static interface View extends Manager.View<CountryManager> {
    }

    public static interface Listener {
        void onSearch(
            List<Country> countries,
            List<Country> selectedCountries);
        void onAdd(Country country);
        void onRemove(Country country);
        void onClear(List<Country> selectedCountries);
    }

    private List<Listener> listeners = new ArrayList<Listener>();

    private ClientRequest.Listener<List<Country>> searchRequestListener;

    private String lastQuery;
    private ClientRequest lastSearchRequest;

    private List<Country> selectedCountries = new ArrayList<Country>();

    public CountryManager() {
        searchRequestListener = new ClientRequest.Listener<List<Country>>() {
            public void onSuccess(
                    ClientRequest request,
                    List<Country> list) {

                for (Listener listener : listeners) {
                    listener.onSearch(list, selectedCountries);
                }
            }

            public void onFailure(
                    ClientRequest request,
                    ClientRequest.Error error) {
            }
        };
    }

    public List<Country> getSelectedCountries() {
        return selectedCountries;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void clearSearch() {
        lastQuery = null;
    }

    public void search(String query) {
        if (query.equals(lastQuery)) {
            return;
        }

        if (lastSearchRequest != null) {
            lastSearchRequest.cancel();
        }

        lastQuery = query;
        lastSearchRequest = WBIExplorationService.queryCountryList(
            query, searchRequestListener);
    }

    public void add(Country country) {
        if (!selectedCountries.contains(country)) {
            selectedCountries.add(country);

            for (Listener listener : listeners) {
                listener.onAdd(country);
            }
        }
    }

    public void remove(Country country) {
        if (selectedCountries.contains(country)) {
            selectedCountries.remove(country);

            for (Listener listener : listeners) {
                listener.onRemove(country);
            }
        }
    } 

    public void toggle(Country country) {
        if (selectedCountries.contains(country)) {
            remove(country);
        } else {
            add(country);
        }
    }

    public void clear() {
        for (Listener listener : listeners) {
            listener.onClear(selectedCountries);
        }

        selectedCountries.clear();
    }
}
