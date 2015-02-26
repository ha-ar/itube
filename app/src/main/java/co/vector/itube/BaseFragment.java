package co.vector.itube;

import android.app.Fragment;
import android.content.ClipData;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;

import java.util.ArrayList;

/**
* Created by android on 11/11/14.
*/
public class BaseFragment extends Fragment {
    AQuery aq ;
    View rootView;ArrayList<ClipData.Item> gridArray;
    static GridView list_Language;BaseClass baseClass;
    public  static LanguageAdapter LanguageAdapter;
    public BaseFragment() {
    }
    public  BaseFragment newinstance ()
    {
        BaseFragment fragment = new BaseFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        baseClass = ((BaseClass) getActivity().getApplicationContext());
        if(baseClass.isTabletDevice(getActivity()))
        {
            rootView = inflater.inflate(R.layout.fragment_language_tablet,
                    container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_base,
                    container, false);
        }
        aq = new AQuery(getActivity(),rootView);
        BaseActivity.language.setText("Select Language");
        list_Language = (GridView) rootView.findViewById(R.id.gridView_language);
        list_Language.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(gridArray.get(position).getText().toString().contains("Hindi"))
                {
                    baseClass.setLanguage("Hindi");
                }
                if(gridArray.get(position).getText().toString().contains("Farsi"))
                {
                    baseClass.setLanguage("Iranian");
                }
                if(gridArray.get(position).getText().toString().contains("Arabic"))
                {
                    baseClass.setLanguage("Arabic");
                }
                if(gridArray.get(position).getText().toString().contains("Turkish"))
                {
                    baseClass.setLanguage("Turkish");
                }
                if(gridArray.get(position).getText().toString().contains("English"))
                {
                    baseClass.setLanguage("English");
                }
                if(gridArray.get(position).getText().toString().contains("German"))
                {
                    baseClass.setLanguage("German");
                }
                if(gridArray.get(position).getText().toString().contains("Spanish"))
                {
                    baseClass.setLanguage("Spanish");
                }
                if(gridArray.get(position).getText().toString().contains("French"))
                {
                    baseClass.setLanguage("French");
                }
                if(gridArray.get(position).getText().toString().contains("Italian"))
                {
                    baseClass.setLanguage("Italian");
                }
                if(gridArray.get(position).getText().toString().contains("Russian"))
                {
                    baseClass.setLanguage("Russian");
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new CategoryFragment().
                                newinstance()).addToBackStack(null).commit();
            }
        });
        prepareLanguageGrid();
        return rootView;
    }
    public  void  prepareLanguageGrid(){

        gridArray = new ArrayList<ClipData.Item>();
        gridArray.clear();
        gridArray.add(new ClipData.Item("Farsi | فارسی"));
        gridArray.add(new ClipData.Item("Arabic | عربى"));
        gridArray.add(new ClipData.Item("Hindi | هندى"));
        gridArray.add(new ClipData.Item("Turkish | تركى"));
        gridArray.add(new ClipData.Item("Spanish | اسپانيايى"));
        gridArray.add(new ClipData.Item("English | انگليسى"));
        gridArray.add(new ClipData.Item("German | آلمانى"));
        gridArray.add(new ClipData.Item("Italian | ايتاليايى"));
        gridArray.add(new ClipData.Item("French | فرانسوى"));
        gridArray.add(new ClipData.Item("Russian | روسى"));
        if(baseClass.isTabletDevice(getActivity()))
        {
            LanguageAdapter = new LanguageAdapter(getActivity(),R.layout.layout_language_listmaker_tablet,gridArray);
        }
        else {
            LanguageAdapter = new LanguageAdapter(getActivity(), R.layout.layout_language_list_maker, gridArray);
        }
       list_Language.setAdapter(LanguageAdapter);
    }
}