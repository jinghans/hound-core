package com.yeezhao.hound.util;


import org.junit.Test;

public class TestTranslator {

	@Test
	public void testTrans(){
		String text = "My research focuses on aspects of the geography of biodiversity.My formal training wasin tropical ornithology, with a particular focus on systematics.As such, one component of my research focuses on the alpha taxonomy of birds, as well as on the phylogeny of recently radiated clades of birds.Tied to this focus is work with the basic geography of bird distributions, and with the composition of local avifaunas, based on detailed site inventories and scientific collections around the world.My work with the geographic and ecology of species' distributions, however, has taken me into other fields, including conservation biology and planning, invasive species biology, and disease transmission systems.In the latter field, my work has focused on numerous disease systems, including Chagas Disease, malaria, dengue, leischmaniasis, and ebola/Marburg.In general, my work is collaborative in nature, and usually involves geographers, computer scientists, and biologists. Pete Hosner, EEB doctoral candidate and Ornithology student mentored by Rob Moyle, received notification that his NSF Doctoral Dissertation Improvement Grant proposal has been recommended for funding. The grant, entitled TESTING THE PLEISTOCENE AGGREGATE ISLAND COMPLEX (PAIC) MODEL OF DIVERSIFICATION IN CO-DISTRIBUTED AVIAN LINEAGES, has been recommended for funding for $14,866 over 24 months. The project will use multilocus DNA sequence data to discover whether there is a link between climate and sea level changes and diversification in eight polytypic bird species endemic to the Philippines. focuses on aspects of the geography of biodiversity.My formal training wasin tropical ornithology, with a particular focus on systematics.As such, one component of my research focuses on the alpha taxonomy of birds, as well as on the phylogeny of recently radiated clades of birds.Tied to thisfocuses on aspects of the geography of biodiversity.My formal training wasin tropical ornithology, with a particular focus on systematics.As such, one component of my research focuses on the alpha taxonomy of birds, as well as on the phylogeny of recently radiated clades of birds.Tied to thisfocuses on aspects of the geography of biodiversity.My formal training wasin tropical ornithology, with a particular focus on systematics.As such, one component of my research focuses on the alpha taxonomy of birds, as well as on the phylogeny of recently radiated clades of birds.Tied to thisfocuses on aspects of the geography of biodiversity.My formal training wasin tropical ornithology, with a particular focus on systematics.As such, one component of my research focuses on the alpha taxonomy of birds, as well as on the phylogeny of recently radiated clades of birds.Tied to this";
		TranslateEn2Ch translator = new TranslateEn2Ch();
		String rs = translator.translateP(text.substring(0 , 1800));
		System.out.println(rs);
//		int pos = rs.indexOf("]]");
//		String substr = rs.substring(1, pos + 2);
//		Gson gson = new Gson();
//		@SuppressWarnings("unchecked")
//		List<ArrayList<String>> sentences = gson.fromJson(substr, List.class);
//		for(ArrayList<String> sentence : sentences){
//			System.out.println(sentence.get(0));
//		}
	}
}
