package com.MDS.ThesisMDS;

import com.MDS.ThesisMDS.frontend.views.MainView;
import com.MDS.ThesisMDS.frontend.views.orderings.HilbertView;
import com.MDS.ThesisMDS.frontend.views.orderings.PeanoView;
import com.MDS.ThesisMDS.frontend.views.orderings.ZOrderView;
import com.MDS.ThesisMDS.frontend.views.tree.PSTView;
import com.MDS.ThesisMDS.frontend.views.tree.PointQuadView;
import com.MDS.ThesisMDS.frontend.views.tree.RangeView;
import com.MDS.ThesisMDS.frontend.views.tree.TrieQuadView;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.ComponentResizeListener;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.jdal.annotation.SerializableProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import javax.servlet.annotation.WebServlet;

@SpringUI
@Widgetset("AppWidgetset")
@SerializableProxy
public class MyVaadinUI extends UI {

    public static final String MAINFORM = "mainform";
    public static final String PSTREE = "psttree";
    public static final String TRIEQUADTREE = "triequadtree";
    public static final String POINTQUADTREE = "pointquadtree";
    public static final String RANGETREE = "rangetree";
    public static final String HILBERTVIEW = "hilbertview";
    public static final String ZORDERVIEW = "zorderview";
    public static final String PEANOVIEW = "peanoview";
    private static final long serialVersionUID = -2006622339916372647L;
    private static final int TOPMARGIN = 20; //ovladaci prvky VAADINU vždy 20px
    private static final int FRAMERATE = 30;
    private static final String LABELPROJECTNAME = "<span style='font-size:24px;'>MDSVisualization</span>";
    private static final String LABELPROJECTDESC = "<span style='color:gray;font-size:9px;vertical-align:bottom;'>" +
            "diploma thesis for the <a href='https://fei.upce.cz/' target='_blank'>University of " +
            "Pardubice</a></span>";
    @Autowired
    Navigator navigator;
    //Vypocet velikosti pracovni plochy
    private int _MaxHeight;
    private int _remainingWidth, _remaingHeight;
    private int _navigationHeight;
    private Registration _lastPool = null;
    private TabSheet tabsheet;
    private HorizontalLayout header;

    //services
    @Autowired
    private EventBus.UIEventBus eventBus;
    @Autowired
    private MainView mainView;
    @Autowired
    private PSTView pstView;
    @Autowired
    private RangeView rangeView;
    @Autowired
    private TrieQuadView trieQuadView;
    @Autowired
    private PointQuadView pointQuadView;
    @Autowired
    private HilbertView hilbertView;
    @Autowired
    private ZOrderView zOrderView;
    @Autowired
    private PeanoView peanoView;


    private View lastView;
    private View newView;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        addComponents();
        addListeners();

        changeView(PSTREE);
        changeView(MAINFORM);
    }

    private void addComponents() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(false);
        layout.setMargin(false);

        //header
        header = new HorizontalLayout();
        header.setSpacing(false);
        header.setMargin(new MarginInfo(true, false, false, true));
        header.addComponent(new Label(LABELPROJECTNAME, ContentMode.HTML));
        header.addComponent(new Label(LABELPROJECTDESC, ContentMode.HTML));
        layout.addComponent(header);

        tabsheet = new TabSheet();
        addTabs(tabsheet);
        layout.addComponentsAndExpand(tabsheet);
        setContent(layout);

    }

    private void addTabs(TabSheet tabsheet) {
        tabsheet.addTab(mainView, "Navigation");
        tabsheet.addTab(pstView, "Priority Search Tree");
        tabsheet.addTab(rangeView, "Range tree");
        tabsheet.addTab(trieQuadView, "Quad tree - Trie based");
        tabsheet.addTab(pointQuadView, "Quad tree - Point based");
        tabsheet.addTab(hilbertView, "Hilbert");
        tabsheet.addTab(zOrderView, "ZOrder");
        tabsheet.addTab(peanoView, "Peano");
        navigator.addView(mainView.getClass().getSimpleName(), mainView);
        navigator.addView(pstView.getClass().getSimpleName(), pstView);
        navigator.addView(rangeView.getClass().getSimpleName(), rangeView);
        navigator.addView(trieQuadView.getClass().getSimpleName(), trieQuadView);
        navigator.addView(pointQuadView.getClass().getSimpleName(), pointQuadView);
        navigator.addView(hilbertView.getClass().getSimpleName(), hilbertView);
        navigator.addView(zOrderView.getClass().getSimpleName(), zOrderView);
        navigator.addView(peanoView.getClass().getSimpleName(), peanoView);
    }

    private void addListeners() {
        tabsheetSizeChanged(tabsheet);
        tabSheetTabChange(tabsheet);
    }

    private void tabSheetTabChange(TabSheet tabsheet) {
        tabsheet.addSelectedTabChangeListener((TabSheet.SelectedTabChangeListener) event -> {
            new SizeReporter(header).addResizeListenerOnce((ComponentResizeListener) event1 -> browserWindowSizeChanged(event1));
            if (lastView != null) {
                eventBus.unsubscribe(lastView);
            }
            navigator.navigateTo(event.getTabSheet().getSelectedTab().getClass().getSimpleName());
            if (navigator.getCurrentView() != null) {
                lastView = null;
                lastView = navigator.getCurrentView();
                eventBus.subscribe(navigator.getCurrentView());
            }
        });
    }

    private void tabsheetSizeChanged(TabSheet tabsheet) {
        SizeReporter sizeReporter = new SizeReporter(tabsheet);
        sizeReporter.addResizeListener((ComponentResizeListener) event -> {
            _MaxHeight = event.getHeight();
            if (_navigationHeight != 0) {
                _remaingHeight = _MaxHeight - _navigationHeight - TOPMARGIN;
            }
        });
    }

    //pokud dojde k resize formuláře musíme zavolat refresh
    private void browserWindowSizeChanged(ComponentResizeEvent event) {
        _navigationHeight = event.getHeight();
        _remaingHeight = _MaxHeight - event.getHeight() - TOPMARGIN;
        _remainingWidth = getPage().getBrowserWindowWidth() - TOPMARGIN;
      //  navigator.navigateTo(navigator.getCurrentView().getClass().getSimpleName());
    }


    public void changeView(String view) {
        switch (view.toLowerCase()) {
            case MAINFORM:
                tabsheet.setSelectedTab(mainView);

                break;
            case PSTREE:
                tabsheet.setSelectedTab(pstView);
                break;
            case RANGETREE:
                tabsheet.setSelectedTab(rangeView);
                break;
            case TRIEQUADTREE:
                tabsheet.setSelectedTab(trieQuadView);
                break;
            case POINTQUADTREE:
                tabsheet.setSelectedTab(pointQuadView);
                break;
            case HILBERTVIEW:
                tabsheet.setSelectedTab(hilbertView);
                break;
            case ZORDERVIEW:
                tabsheet.setSelectedTab(zOrderView);
                break;
            case PEANOVIEW:
                tabsheet.setSelectedTab(peanoView);
                break;
        }
    }

    public int get_remainingWidth() {
        return _remainingWidth;
    }

    public int get_remaingHeight() {
        return _remaingHeight;
    }

    public View getLastView() {
        return lastView;
    }

    public void setLastView(View lastView) {
        this.lastView = lastView;
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class)
    public static class Servlet extends VaadinServlet {
    }


}
