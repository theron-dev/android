package org.hailong.framework.data;

import java.util.ArrayList;
import java.util.List;

import org.hailong.framework.data.annotation.DataEntity;
import org.hailong.framework.data.predicate.Exp;
import org.hailong.framework.data.predicate.Field;
import org.hailong.framework.data.predicate.Value;



public class DataFetchRequest<T extends DataObject>{
	
	private DataEntity _dataEntity;
	private DataSort[] _dataSorts;
	private int _fetchLimit;
	private int _fetchOffset;
	private Exp _dataPredicate;
	

	public DataFetchRequest(){
	}
	
	public DataEntity getDataEntity(){
		return _dataEntity;
	}
	
	public void setDataEntity(DataEntity dataEntity){
		_dataEntity = dataEntity;
	}
	
	public DataSort[] getDataSorts(){
		return _dataSorts;
	}
	
	public void setDataSorts(DataSort ... dataSorts){
		_dataSorts = dataSorts;
	}
	
	public int getFetchLimit(){
		return _fetchLimit;
	}
	
	public void setFetchLimit(int fetchLimit){
		_fetchLimit = fetchLimit;
	}
	
	public int getFetchOffset(){
		return _fetchOffset;
	}

	public void setFetchOffset(int fetchOffset){
		_fetchOffset = fetchOffset;
	}
	
	public void setDataPredicate(Exp dataPredicate){
		_dataPredicate = dataPredicate;
	}
	
	public Exp getDataPredicate(){
		return _dataPredicate;
	}
	
	boolean filter(IDataEntityRawData rawData){
		if(_dataPredicate !=null){
			return _dataPredicate.result(rawData);
		}
		return true;
	}
	
	List<Integer> store(List<IDataEntityRawData> datas){
		List<Integer> indexs = new ArrayList<Integer>(datas.size());
		
		int j;
		for(int i = 0;i<datas.size();i++){
			IDataEntityRawData data = datas.get(i);
			for(j=0;j<indexs.size();j++){
				IDataEntityRawData d = datas.get(indexs.get(j));
				double rs = 0;
				
				if(_dataSorts !=null){
					for(DataSort dataSort : _dataSorts){
						Field field = new Field(dataSort.getField());
						Object v1 = field.getValue(data);
						Object v2 = field.getValue(d);
						if(dataSort.getSortType() == DataSortType.ASC){
							rs = Value.compare(v1, v2);
						}
						else{
							rs = - Value.compare(v1, v2);
						}
						if(rs !=0 ){
							break;
						}
					}
				}
				
				if(rs == 0){
					rs = data.getRawId() - d.getRawId();
				}
				if(rs <0){
					break;
				}
			}
			if(j<indexs.size()){
				indexs.add(j, i);
			}
			else{
				indexs.add(i);
			}
		}
		
		return indexs;
	}
}
