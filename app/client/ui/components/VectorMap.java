/*
 * WBI Indicator Explorer
 *
 * Copyright 2015 Sebastian Nogara <snogaraleal@gmail.com>
 *
 * This file is part of WBI.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package client.ui.components;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import client.ClientConf;
import client.ui.components.utils.Script;

/**
 * Widget displaying a map.
 * http://jvectormap.com
 */
public class VectorMap extends Composite {
    /**
     * Map displayed by a {@link VectorMap}.
     */
    public static enum Visual {
        /**
         * Map of Europe.
         */
        EUROPE(
            "europe_mill_en",
            ClientConf.asset("js/jvectormap/jvectormap-europe-mill-en.js")),

        /**
         * Map of the World.
         */
        WORLD(
            "world_mill_en",
            ClientConf.asset("js/jvectormap/jvectormap-world-mill-en.js"));

        /**
         * Map identifier.
         */
        private String name;

        /**
         * Map script.
         */
        private String script;

        /**
         * Initialize {@code Visual}.
         *
         * @param name Map identifier.
         * @param script Map script.
         */
        private Visual(String name, String script) {
            this.name = name;
            this.script = script;
        }

        /**
         * Get map identifier.
         *
         * @return Map identifier.
         */
        public String getName() {
            return name;
        }

        /**
         * Get map script.
         *
         * @return Map script.
         */
        public String getScript() {
            return script;
        }
    }

    public interface VectorMapUiBinder extends UiBinder<Widget, VectorMap> {}
    private static VectorMapUiBinder uiBinder =
        GWT.create(VectorMapUiBinder.class);

    /**
     * Element containing the map.
     */
    @UiField
    public DivElement div;

    /**
     * Library base script.
     */
    public static final String BASE_SCRIPT =
        ClientConf.asset("js/jvectormap/jvectormap-2.0.1.min.js");

    /**
     * {@link Script.Loader} for {@link VectorMap#BASE_SCRIPT}.
     */
    public static final Script.Loader BASE_SCRIPT_LOADER =
        new Script.Loader(BASE_SCRIPT, Script.JQUERY);

    /**
     * Map displayed.
     */
    private Visual visual;

    /**
     * {@link Script.Loader} for the current {@link Visual}.
     */
    private Script.Loader visualScriptLoader;

    /**
     * Whether the current {@code Visual} is loaded.
     */
    private boolean visualLoaded = false;

    /**
     * Initialize {@code VectorMap}.
     */
    public VectorMap() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Initialize {@code VectorMap}.
     *
     * @param visual {@link Visual} that specifies the map to display.
     */
    public VectorMap(Visual visual) {
        this();
        setVisual(visual);
    }

    /**
     * Set the {@link Visual} that specifies the map to display.
     *
     * @param visual Map to display.
     */
    public void setVisual(Visual visual) {
        this.visual = visual;
        visualScriptLoader = new Script.Loader(
            visual.getScript(), BASE_SCRIPT_LOADER);
    }

    /**
     * Load the required scripts.
     *
     * @param callback {@code Runnable} called when ready.
     */
    private void loadVisual(final Runnable callback) {
        visualScriptLoader.load(new Runnable() {
            @Override
            public void run() {
                if (!visualLoaded) {
                    loadVisual(visual.getName());
                    visualLoaded = true;
                }

                callback.run();
            }
        });
    }

    /**
     * Initialize map graphics.
     *
     * @param visualName Map identifier from a loaded {@link Visual}.
     */
    private native void loadVisual(String visualName) /*-{
        (function (that, $, jvm) {
            var div = $(that.@client.ui.components.VectorMap::div);

            that.map = new jvm.Map({
                container: div,
                map: visualName,
                backgroundColor: 'transparent',
                zoomOnScroll: false,
                regionStyle: {
                    initial: {
                        fill: '#DDDDDD'
                    },
                    hover: {
                        cursor: 'pointer'
                    }
                },
                series: {
                    regions: [{
                        values: {},
                        scale: ['#DDDDDD', '#666666'],
                        normalizeFunction: 'linear'
                    }],
                },
                onRegionTipShow: function(event, label, code) {
                    var data = that.series.values;

                    if (data[code] != undefined) {
                        label.html(
                            label.text() + ' (' +
                            Math.floor(data[code] * 10) / 10 + ')');
                    }
                }
            });

            that.series = that.map.series.regions[0];
        })(this, $wnd.jQuery, $wnd.jvm);
    }-*/;

    /**
     * Get a {@code JavaScriptObject} containing map information as
     * required by the library.
     *
     * @param data {@code Map} of ISO codes and values.
     * @return {@code JavaScriptObject} as required by the library.
     */
    private JavaScriptObject dataToJSObject(Map<String, Double> data) {
        if (data == null) {
            return null;
        }

        JSONObject dataObject = new JSONObject();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataObject.put(entry.getKey(), new JSONNumber(entry.getValue()));
        }

        return dataObject.getJavaScriptObject();
    }

    /**
     * Set data displayed by the map.
     *
     * @param data {@code Map} of ISO codes and values.
     */
    public void setData(final Map<String, Double> data) {
        loadVisual(new Runnable() {
            @Override
            public void run() {
                setData(dataToJSObject(data));
            }
        });
    }

    /**
     * Display the specified data.
     *
     * @param data Map information as required by the library.
     * @see VectorMap#dataToJSObject
     */
    private native void setData(JavaScriptObject data) /*-{
        (function (that, $, jvm) {
            var div = $(that.@client.ui.components.VectorMap::div);

            that.map.reset();

            that.series.params.min = undefined;
            that.series.params.max = undefined;
            that.series.setValues(data);

            setTimeout(function () {
                that.map.updateSize();
            });
        })(this, $wnd.jQuery, $wnd.jvm);
    }-*/;
}
