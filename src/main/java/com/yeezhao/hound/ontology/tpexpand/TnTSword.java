package com.yeezhao.hound.ontology.tpexpand;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理sword的笛卡尔积
 * @author Administrator
 *
 */
public class TnTSword {
	private int category;
	private List<List<Node>> keywords=new ArrayList<List<Node>>();
	
	public TnTSword(int category,List<List<Node>> keywords){
		this.category=category;
		this.keywords=keywords;
	}

	public List<List<Node>> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<List<Node>> keywords) {
		this.keywords = keywords;
	}
	
	
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public SWords getSwords(){
//		//TODO 这个留着，以后wk来实现

		List<List<Node>> swordsNode=decareList(keywords);
		if (swordsNode == null || swordsNode.isEmpty()) {
			return null;
		}
		List<SWord> swords=new ArrayList<SWord>(swordsNode.size());
		for (List<Node> nodes : swordsNode) {
			SWord sword=new SWord(nodes);
			swords.add(sword);
		}
		
		return new SWords(category, swords);
		
	}
	
	
	public static List<List<Node>> decareList(List<List<Node>> keywords){
		if (keywords.size()==0) {
			return null;
		}
		
		if (keywords.size()==1) {
			List<Node> t= keywords.get(0);
			List<List<Node>> left= new ArrayList<List<Node>>();
			for (Node node : t) {
				List<Node> temp=new ArrayList<Node>();
				temp.add(node);
				left.add(temp);
			}
			return left;
		}
		
		List<Node> nodes=keywords.get(0);
		List<List<Node>> left= new ArrayList<List<Node>>();
		
		for (Node node : nodes) {
			List<Node> t=new ArrayList<Node>();
			t.add(node);
			left.add(t);
		}
		for (int i = 1; i<keywords.size(); i++) {
			List<Node> right=keywords.get(i);
			List<List<Node>> temp= new ArrayList<List<Node>>();
			for (List<Node> lt : left) {
				for (Node node : right) {
					List<Node> t=new ArrayList<Node>();
					t.addAll(lt);
					t.add(node);
					temp.add(t);
				}
			}
			left=temp;
		}
		return left;
	}

	
	public static void main(String[] args) {
		Node a1=new Node("a1", "a");
		Node a2=new Node("a2", "a");
		Node a3=new Node("a3", "a");
		Node a4=new Node("a4", "a");
		Node a5=new Node("a5", "a");
		
		List<Node> a=new ArrayList<Node>();
		a.add(a1);a.add(a2);a.add(a3);a.add(a4);a.add(a5);
		
		
		Node b1=new Node("b1", "b");
		Node b2=new Node("b2", "b");
		Node b3=new Node("b3", "b");
		Node b4=new Node("b4", "b");
		Node b5=new Node("b5", "b");
		
		
		List<Node> b=new ArrayList<Node>();
		b.add(b1);b.add(b2);b.add(b3);b.add(b4);b.add(b5);
		
		Node c1=new Node("c1", "c");
		Node c2=new Node("c2", "c");
		Node c3=new Node("c3", "c");
		Node c4=new Node("c4", "c");
		Node c5=new Node("c5", "c");
		
		
		List<Node> c=new ArrayList<Node>();
		c.add(c1);c.add(c2);c.add(c3);c.add(c4);c.add(c5);
		
		List<List<Node>> tntNodes=new ArrayList<List<Node>>();
		tntNodes.add(a);
		tntNodes.add(b);
		tntNodes.add(c);
		
		System.out.println(tntNodes);
		System.out.println("--------------------------");
		tntNodes =decareList(tntNodes);
		
		System.out.println(tntNodes );
		
		for (List<Node> list : tntNodes) {
			System.out.println(list);
		}
		
		System.out.println("---------------------------");
		
		List<List<Node>> t2=new ArrayList<List<Node>>();
		t2.add(a);
		System.out.println(decareList(t2));
	}
}
