package com.salk.mycircadianclock.information.faq;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.information.getting_strated.GettingStartedActivity;
import com.salk.mycircadianclock.information.getting_strated.GettingStartedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;
    private ExpandableListView getting_strated_list;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private GettingStartedAdapter gettingStartedAdapter;
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(FaqActivity.this);

            setContentView(R.layout.activity_getting_started);

            init();

            intializacommonclass();

            set_list_dat();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo methos to intialize XML views
    private void init(){
        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            getting_strated_list = findViewById(R.id.faqlist);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void intializacommonclass(){

        try{

            new TabbarClick().click(FaqActivity.this, tabbar, rel_main, "info");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo set dat in expandable list
    private void set_list_dat(){

        prepareListData();

        gettingStartedAdapter = new GettingStartedAdapter(FaqActivity.this, listDataHeader, listDataChild);

        // setting list adapter
        getting_strated_list.setAdapter(gettingStartedAdapter);

        getting_strated_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {


                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    getting_strated_list.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });

    }

    //Todo set data for french language
    private void FR_prepareListData() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("COMMENT ENREGISTRER LES ALIMENTS ET LES BOISSONS");
        listDataHeader.add("COMMENT AJOUTER UNE ANNOTATION ?");
        listDataHeader.add("COMMENT ENREGISTRER LE SOMMEIL ?");
        listDataHeader.add("COMMENT ENREGISTRER UNE ACTIVITE PHYSIQUE ?");
        listDataHeader.add("EST-CE QUE JE PEUX ENREGISTRER PLUSIEURS ALIMENTS EN UNE FOIS ?");
        listDataHeader.add("EST-IL EST IMPORTANT D’ENREGISTRER LA CONSOMMATION DE CAFÉINE ?");
        listDataHeader.add("EST-CE QUE JE PEUX CORRIGER UNE ANNOTATION CHOISIE PAR ERREUR ?");
        listDataHeader.add("EST-CE QUE JE PEUX SUPPRIMER UN ENREGISTREMENT ?");
        listDataHeader.add("COMBIEN DE TEMPS DOIS-JE UTILISER L’APPLICATION ?");
        listDataHeader.add("PUIS-JE UTILISER L’APPLICATION AUSSI SANS CONNEXION (OFFLINE) ?");
        listDataHeader.add("QU’EST-CE QUE C’EST UN FEEDOGRAM ?");
        listDataHeader.add("POURQUOI JE NE PEUX PAS VOIR MON FEEDOGRAM ?");
        listDataHeader.add("QUE SIGNIFIE « ACTIVITY LEVEL » ?");
        listDataHeader.add("PUIS-JE ENTRER LES EXERCICES APRÈS COUP ?");
        listDataHeader.add("QU’EST QUE C’EST LE GRAPHIQUE DES EXERCICES ?");
        listDataHeader.add("PUIS-JE SYNCHRONISER LES DONNÉES DE MON TRAQUEUR D'ACTIVITÉ AVEC L’APP ?");




        List<String> sub_question1 = new ArrayList<String>();
        sub_question1.add("Étape 1: Prenez en photo l’aliment ou la boisson que vous allez consommer. Il peut s’agir de toutes sortes d’éléments comme un goûter, un plat principal, des grignotages, un verre d’eau, un verre de vin, etc. Si vous consommez plusieurs aliments en même temps, veuillez prendre une photo de chaque aliment. \\n\\nÉtape 2: Choisissez la catégorie appropriée : aliments, boissons ou eau. \\n\\nÉtape 3: Tapez le nom de l’aliment ou de la boisson ou choisissez un des termes prédéfinis. Vous pouvez ajouter un nombre d’annotation illimité pour décrire tous les éléments sur la photo. \\n\\nÉtape 4: Cliquez sur « sauver ». Si vous avez oublié d‘enregistrer une consommation, vous pouvez cliquer sur l’icône « couverts » sur la page d’accueil (pour voir l’icône « couverts », cliquez d’abord sur l’icône + sur le bas de la page d’accueil). Cela vous mènera directement à la page d’annotations (étape 3). N’oubliez pas d’enregistrer le temps de la consommation avant de sauvegarder."
        );

        List<String> sub_question2 = new ArrayList<String>();
        sub_question2.add("Vous pouvez taper l‘annotation dans la zone de texte. Si vous cliquez sur la touche d’entrée, l’annotation est ajoutée sur la liste en-dessous de la zone de texte. Au bout de quelques jours, les annotations d’aliments et boissons que vous consommez régulièrement seront affichées tout en haut de la liste d’annotations. Vous pourrez désormais seulement cliquer sur le terme approprié pour enregistrer votre consommation.");

        List<String> sub_question3 = new ArrayList<String>();
        sub_question3.add("Étape 1: Cliquez sur l‘icône sommeil sur la page d’accueil (pour voir cette icône, cliquez d’abord sur l’icône + sur le bas de la page d’accueil).\\n\\nÉtape 2: Cliquez sur le bouton « début du sommeil » pour lancer l’enregistrement.\\n\\nÉtape 3: Au moment où vous vous réveillez, cliquez sur le bouton « fin du sommeil » pour stopper l’enregistrement. Vous pouvez indiquer si vous vous sentez reposé(e) ou pas en cliquant sur l’emoticon. En cliquant sur la touche « sauver », votre durée de sommeil sera calculée, enregistrée et visible sur le petit tableau de l’écran.\\nSi vous désirez ajouter les temps de sommeil pour des nuits précédentes, vous pouvez enregistrer les données manuellement en cliquant sur le bouton « enregistrer un sommeil précédent » qui se trouve sous le bouton « début du sommeil.");

        List<String> sub_question4 = new ArrayList<String>();
        sub_question4.add("Étape 1: Sur la page d’accueil, cliquez sur l’icône + et ensuite sur l’icône d’activité.\\n\\nÉtape 2: Ajoutez une activité et choisissez l’intensité, ensuite cliquez sur le bouton « débuter l’exercice ». Si vous désirez enregistrer un exercice précédent, cliquez sur le bouton « enregistrer un exercice précédent».\\n\\nÉtape 3: Au moment où vous finissez l’exercice, cliquez sur le bouton « terminer l’exercice ». L’exercice est ajouté automatiquement, vous avez seulement besoin de cliquer sur « sauver » si vous enregistrez un exercice précédent. Si vous cliquez sur « annuler » l’exercice ne sera pas enregistré.");

        List<String> sub_question5 = new ArrayList<String>();
        sub_question5.add("Oui, il est possible d’enregistrer plusieurs aliments à la fois. Veuillez-vous référer à la section comment enregistrer les aliments et les boissons ci-dessus.");

        List<String> sub_question6 = new ArrayList<String>();
        sub_question6.add("Oui, il est aussi important d’enregistrer la consommation de caféine, de boissons sans calories (par ex. Coca Zero) et de thé. La caféine a un effet sur votre cerveau, votre rythme de sommeil et le métabolisme. Ceci est donc important pour mieux comprendre quels effets la caféine a sur la restriction alimentaire chronologique.");

        List<String> sub_question7 = new ArrayList<String>();
        sub_question7.add("Si vous désirez supprimer une annotation, vous pouvez appuyer pendant environ 5 secondes sur le terme dans la liste des annotations. Cette action devra être confirmée en cliquant sur « oui » pour une suppression définitive.");

        List<String> sub_question8 = new ArrayList<String>();
        sub_question8.add("Non, une fois sauvegardé, un enregistrement d’aliments ou boissons ne peut plus être supprimé. Nous sommes conscients du fait qu’il va y avoir un certain nombre d’erreurs d’enregistrement.");

        List<String> sub_question9 = new ArrayList<String>();
        sub_question9.add("Pour avoir des données complètes, nous vous demandons d’utiliser l’application pendant 4 semaines (toute la période d’observation). Avec votre accord et si vous correspondez aux critères, vous pourrez prolonger l’étude avec une intervention simple et utiliser l’app pour six mois additionnels (sept mois au total).");

        List<String> sub_question10 = new ArrayList<String>();
        sub_question10.add("Oui, vous pouvez utiliser l’app aussi en mode avion (sans connexion) ; toutes les données enregistrées seront transférées une fois que vous vous reconnecterez. Par contre vous ne pourrez pas accéder au feedogram ou à l’historique."
        );

        List<String> sub_question11 = new ArrayList<String>();
        sub_question11.add("Un feedogram résume la chronologie de tous les aliments et boissons consommés, et des médicaments si vous les avez enregistrés. Les marques en vert représentent les aliments et boissons, l’eau en rouge et les médicaments en orange. En cliquant sur l’une de ces marques, vous pourrez voir les détails de l’enregistrement. Sous le feedogram, vous pouvez voir une ligne horizontale verte représentant la durée moyenne des heures où vous mangez et buvez pendant la semaine. La ligne horizontale orange montre la durée moyenne des heures où vous mangez et buvez pendant le week-end.");

        List<String> sub_question12 = new ArrayList<String>();
        sub_question12.add("Le feedogram sera seulement disponible après la phase d’observation, lorsque l’intervention sera en court.");

        List<String> sub_question13 = new ArrayList<String>();
        sub_question13.add("« Activity Level » signifie l’intensité de l’activité physique que vous êtes en train de faire. Vous pouvez évaluer ceci à trois différentes intensités : légère (Light), aucune sensation d’effort (possible d’avoir une conversation très facilement) ; modérée (Moderate), une sensation d’effort présente (la communication verbale n’est néanmoins pas perturbée) ; intense (Intense), une grande sensation d’effort, impossibilité de parler ou uniquement à dire quelque mots.");

        List<String> sub_question14 = new ArrayList<String>();
        sub_question14.add("Oui, vous pouvez entrer les données des exercices de la même façon que si vous le faites au moment de l’exercice. Corrigez uniquement l’heure et la date à laquelle vous avez fait de l’exercice.");

        List<String> sub_question15 = new ArrayList<String>();
        sub_question15.add("Ce graphique retrace les dates et la durée de tous les exercices ou activités (pas par jour). Pour visualiser ce graphique, rendez vous sous Historique (History) et sélectionner Exercices. Attention, vous ne pouvez visualiser ce graphique qu’après avoir complété au moins deux semaines d’étude.");

        List<String> sub_question16 = new ArrayList<String>();
        sub_question16.add("Les données de Google Fit ou Apple Santé sont synchronisées automatiquement avec l’app MyCircadianClock. Si vous avez d’autres appareils ou traqueurs d’activité, synchronisez-le avec Google Fit ou Apple Santé d’abord, puis ces informations se retrouveront automatiquement dans MyCircadianClock.");


        listDataChild.put(listDataHeader.get(0), sub_question1);
        listDataChild.put(listDataHeader.get(1), sub_question2); // Header, Child data
        listDataChild.put(listDataHeader.get(2), sub_question3);
        listDataChild.put(listDataHeader.get(3), sub_question4);
        listDataChild.put(listDataHeader.get(4), sub_question5);
        listDataChild.put(listDataHeader.get(5), sub_question6);
        listDataChild.put(listDataHeader.get(6), sub_question7);
        listDataChild.put(listDataHeader.get(7), sub_question8);
        listDataChild.put(listDataHeader.get(8), sub_question9);
        listDataChild.put(listDataHeader.get(9), sub_question10);
        listDataChild.put(listDataHeader.get(10), sub_question11);
        listDataChild.put(listDataHeader.get(11), sub_question12);
        listDataChild.put(listDataHeader.get(12), sub_question13);
        listDataChild.put(listDataHeader.get(13), sub_question14);
        listDataChild.put(listDataHeader.get(14), sub_question15);
        listDataChild.put(listDataHeader.get(15), sub_question16);


    }

    //Todo set data for english language
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add(getResources().getString(R.string.When_should_I_sleep));
        listDataHeader.add(getResources().getString(R.string.When_should_I_exercise));
        listDataHeader.add(getResources().getString(R.string.How_much_should_I_exercise));
        listDataHeader.add(getResources().getString(R.string.eating_interval_goal_be));
        listDataHeader.add(getResources().getString(R.string.What_are_the_health_benefits_of_eating));
        listDataHeader.add(getResources().getString(R.string.If_I_restrict_my_food));
        listDataHeader.add(getResources().getString(R.string.decided_on_an_eating_interval_goal));
        listDataHeader.add(getResources().getString(R.string.Does_it_matter_when_I_eat));
        listDataHeader.add(getResources().getString(R.string.How_long_do_I_need));
        listDataHeader.add(getResources().getString(R.string.Can_I_sync_my_data));
        listDataHeader.add(getResources().getString(R.string.Can_I_use_the_app_offline));
        listDataHeader.add(getResources().getString(R.string.What_is_the_correct_way));
        listDataHeader.add(getResources().getString(R.string.Can_I_save_multiple_Food));
        listDataHeader.add(getResources().getString(R.string.Does_Caffeine_Count));
        listDataHeader.add(getResources().getString(R.string.How_can_I_edit_an_annotation));
        listDataHeader.add(getResources().getString(R.string.Can_I_delete_an_entry));
        listDataHeader.add(getResources().getString(R.string.Can_I_still_record_food));
        listDataHeader.add(getResources().getString(R.string.What_are_Self));
        listDataHeader.add(getResources().getString(R.string.What_is_feedogram));
        listDataHeader.add(getResources().getString(R.string.Why_cant_feedogram));
        listDataHeader.add(getResources().getString(R.string.What_are_the_marks));
        listDataHeader.add(getResources().getString(R.string.How_do_sleep));
        listDataHeader.add(getResources().getString(R.string.How_medicine_intake));
        listDataHeader.add(getResources().getString(R.string.Can_I_add_my_previous));
        listDataHeader.add(getResources().getString(R.string.What_is_the_activity));
        listDataHeader.add(getResources().getString(R.string.What_is_BMI));
        listDataHeader.add(getResources().getString(R.string.What_is_BMI_chart));
        listDataHeader.add(getResources().getString(R.string.my_blood_sugar));
        listDataHeader.add(getResources().getString(R.string.blood_sugar_chart));
        listDataHeader.add(getResources().getString(R.string.Blood_Pressure));
        listDataHeader.add(getResources().getString(R.string.What_are_Systolic));
        listDataHeader.add(getResources().getString(R.string.Blood_Pressure_Data));


        List<String> sub_question2 = new ArrayList<String>();
        sub_question2.add(getResources().getString(R.string.The_optimal_timing));

        List<String> sub_question3 = new ArrayList<String>();
        sub_question3.add(getResources().getString(R.string.There_is_no_one_exact));

        List<String> sub_question4 = new ArrayList<String>();
        sub_question4.add(getResources().getString(R.string.You_should_consult));

        List<String> sub_question5 = new ArrayList<String>();
        sub_question5.add(getResources().getString(R.string.We_do_not_provide));

        List<String> sub_question6 = new ArrayList<String>();
        sub_question6.add(getResources().getString(R.string.A_majority_of_research));

        List<String> sub_question7 = new ArrayList<String>();
        sub_question7.add(getResources().getString(R.string.healthy_lifestyle));

        List<String> sub_question8 = new ArrayList<String>();
        sub_question8.add(getResources().getString(R.string.start_stop_times));

        List<String> sub_question9 = new ArrayList<String>();
        sub_question9.add(getResources().getString(R.string.Your_body_digests));

        List<String> sub_question10 = new ArrayList<String>();
        sub_question10.add(getResources().getString(R.string.In_order_to));

        List<String> sub_question11 = new ArrayList<String>();
        sub_question11.add(getResources().getString(R.string.Data_from_Google));

        List<String> sub_question12 = new ArrayList<String>();
        sub_question12.add(getResources().getString(R.string.offline_app));

        List<String> sub_question14 = new ArrayList<String>();
        sub_question14.add(getResources().getString(R.string.You_can_type_annotation));

        List<String> sub_question15 = new ArrayList<String>();
        sub_question15.add(getResources().getString(R.string.send_multiple_food));

        List<String> sub_question16 = new ArrayList<String>();
        sub_question16.add(getResources().getString(R.string.caffeine_counts));

        List<String> sub_question17 = new ArrayList<String>();
        sub_question17.add(getResources().getString(R.string.delete_annotation));

        List<String> sub_question18= new ArrayList<String>();
        sub_question18.add(getResources().getString(R.string.item_saved));

        List<String> sub_question19 = new ArrayList<String>();
        sub_question19.add(getResources().getString(R.string.send_data));

        List<String> sub_question20 = new ArrayList<String>();
        sub_question20.add(getResources().getString(R.string.baseline_period));

        List<String> sub_question21 = new ArrayList<String>();
        sub_question21.add(getResources().getString(R.string.feedogram_timeline));

        List<String> sub_question22 = new ArrayList<String>();
        sub_question22.add(getResources().getString(R.string.feedogram_will_become_available));

        List<String> sub_question23 = new ArrayList<String>();
        sub_question23.add(getResources().getString(R.string.Feedogram_menu));


        List<String> sub_question25 = new ArrayList<String>();
        sub_question25.add(getResources().getString(R.string.sleep_goals));

        List<String> sub_question26 = new ArrayList<String>();
        sub_question26.add(getResources().getString(R.string.take_photo));

        List<String> sub_question29  = new ArrayList<String>();
        sub_question29.add(getResources().getString(R.string.previous_exercise));

        List<String> sub_question30 = new ArrayList<String>();
        sub_question30.add(getResources().getString(R.string.activity_chart));

        List<String> sub_question31 = new ArrayList<String>();
        sub_question31.add(getResources().getString(R.string.Body_Mass_Index));

        List<String> sub_question32 = new ArrayList<String>();
        sub_question32.add(getResources().getString(R.string.BMI_chart_displays));

        List<String> sub_question33 = new ArrayList<String>();
        sub_question33.add(getResources().getString(R.string.measure_blood_sugar));

        List<String> sub_question34 = new ArrayList<String>();
        sub_question34.add(getResources().getString(R.string.recorded_blood_sugar));

        List<String> sub_question35 = new ArrayList<String>();
        sub_question35.add(getResources().getString(R.string.measure_blood_pressure));

        List<String> sub_question36 = new ArrayList<String>();
        sub_question36.add(getResources().getString(R.string.heart_beat));

        List<String> sub_question37 = new ArrayList<String>();
        sub_question37.add(getResources().getString(R.string.graph_icon));


        listDataChild.put(listDataHeader.get(0), sub_question2); // Header, Child data
        listDataChild.put(listDataHeader.get(1), sub_question3);
        listDataChild.put(listDataHeader.get(2), sub_question4);
        listDataChild.put(listDataHeader.get(3), sub_question5);
        listDataChild.put(listDataHeader.get(4), sub_question6);
        listDataChild.put(listDataHeader.get(5), sub_question7);
        listDataChild.put(listDataHeader.get(6), sub_question8);
        listDataChild.put(listDataHeader.get(7), sub_question9);
        listDataChild.put(listDataHeader.get(8), sub_question10);
        listDataChild.put(listDataHeader.get(9), sub_question11);
        listDataChild.put(listDataHeader.get(10), sub_question12);
        listDataChild.put(listDataHeader.get(11), sub_question14);
        listDataChild.put(listDataHeader.get(12), sub_question15);

        listDataChild.put(listDataHeader.get(13), sub_question16);
        listDataChild.put(listDataHeader.get(14), sub_question17);
        listDataChild.put(listDataHeader.get(15), sub_question18);
        listDataChild.put(listDataHeader.get(16), sub_question19);
        listDataChild.put(listDataHeader.get(17), sub_question20);
        listDataChild.put(listDataHeader.get(18), sub_question21);
        listDataChild.put(listDataHeader.get(19), sub_question22);
        listDataChild.put(listDataHeader.get(20), sub_question23);
        listDataChild.put(listDataHeader.get(21), sub_question25);
        listDataChild.put(listDataHeader.get(22), sub_question26);
        listDataChild.put(listDataHeader.get(23), sub_question29);
        listDataChild.put(listDataHeader.get(24), sub_question30);
        listDataChild.put(listDataHeader.get(25), sub_question31);
        listDataChild.put(listDataHeader.get(26), sub_question32);
        listDataChild.put(listDataHeader.get(27), sub_question33);
        listDataChild.put(listDataHeader.get(28), sub_question34);
        listDataChild.put(listDataHeader.get(29), sub_question35);
        listDataChild.put(listDataHeader.get(30), sub_question36);
        listDataChild.put(listDataHeader.get(31), sub_question37);

    }
}
