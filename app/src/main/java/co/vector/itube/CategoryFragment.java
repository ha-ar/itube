package co.vector.itube;

import android.app.Fragment;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;

import java.util.ArrayList;

import Models.DurationModel;
import Models.GetAllByCategoryModel;

/**
 * Created by android on 11/17/14.
 */
public class CategoryFragment extends Fragment {
    AQuery aq;
    ArrayList<ClipData.Item> gridArray = new ArrayList<ClipData.Item>();
    View rootView;
    BaseClass baseClass;
    static GridView list_Category;
    public static CategoryAdapter CategoryAdapter;

    public CategoryFragment() {
    }

    public CategoryFragment newinstance() {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseActivity.language.setText("Select Language");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        baseClass =((BaseClass) getActivity().getApplicationContext());
        baseClass.setDurationCounter(0);
        GetAllByCategoryModel.getInstance().items.clear();
        DurationModel.getInstance().items.clear();
        if(baseClass.isTabletDevice(getActivity()))
        {
            rootView = inflater.inflate(R.layout.fragment_category_tablet,
                    container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.layout_gridcategory,
                    container, false);
        }
        aq = new AQuery(getActivity(),rootView);
        BaseActivity.language.setText("Select Category");
        list_Category = (GridView) rootView.findViewById(R.id.gridView_category);
        list_Category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                baseClass.setCategory(gridArray.get(position).getText().toString());
                baseClass.setIsFromFavorites("No");
                baseClass.setDataBy("Category");

                for(int loop=0;loop<baseClass.specifiedKeyData.size();loop++)
                {
                    if( baseClass.getLanguage().contains(baseClass.specifiedKeyData.get(loop).getLanguage()))
                    {
                        if( gridArray.get(position).getText().toString().contains("Movies")) {
                            baseClass.setDuration("long");
                            baseClass.setNewCategory(baseClass.specifiedKeyData.get(loop).getMovie());
                        }
                        if( gridArray.get(position).getText().toString().contains("Cartoons")) {
                            baseClass.setDuration("medium");
                            baseClass.setCategory("Cartoons");
                            baseClass.setNewCategory(baseClass.specifiedKeyData.get(loop).getCartons());
                        }
                        if( gridArray.get(position).getText().toString().contains("Music")) {
                            baseClass.setDuration("medium");
                            baseClass.setNewCategory(baseClass.specifiedKeyData.get(loop).getMusic());
                        }
                        if( gridArray.get(position).getText().toString().contains("Documentaries")) {
                            baseClass.setDuration("long");
                            baseClass.setNewCategory(baseClass.specifiedKeyData.get(loop).getDocumentries());
                        }
                        Log.e("String",baseClass.specifiedKeyData.get(loop).getDocumentries());
                        break;
                    }
                }
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new SongsListViewFragment().
                                newinstance()).addToBackStack(null).commit();
            }
        });
        prepareCategoryGrid();
        return rootView;
    }
    public  void  prepareCategoryGrid(){
        gridArray.clear();
        gridArray.add(new ClipData.Item("Movies"));
        gridArray.add(new ClipData.Item("Cartoons"));
        gridArray.add(new ClipData.Item("Music"));
        gridArray.add(new ClipData.Item("Documentaries"));
        if(baseClass.isTabletDevice(getActivity()))
        {
            CategoryAdapter = new CategoryAdapter(getActivity(),R.layout.layout_category_listmaker_tablet,gridArray);
        }
        else {
            CategoryAdapter = new CategoryAdapter(getActivity(), R.layout.layout_category_list_maker, gridArray);
        }
        list_Category.setAdapter(CategoryAdapter);
    }

}