<!-- eslint-disable vue/multi-word-component-names -->
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
<template>
  <div id="bar">
    <div id="for"></div>
    <div id="against"></div>
    <div id="none"></div>
  </div>

  <div id="count">
    <span id="for">{{ for_total }}</span>/<span id="total">{{ total }}</span> (<span id="abs">{{ none_total }}</span> abstentions) -> <span id="approved">{{ yes }}</span>
  </div>

  <div id="party-selection" v-for="party of parties">
    <div>
      <div id="name">{{ party.name }}</div>
      <div id="buttons">
        <button id="for" :active="party.vote == 0" @click="set_vote(party.name, 0)">Pour</button>
        <button id="against" :active="party.vote == 1" @click="set_vote(party.name, 1)">Contre</button>
        <button id="none" :active="party.vote == 2" @click="set_vote(party.name, 2)">Abstention</button>
        <input type="number" min="0" max="132" :value="party.seats" @input="event => redis_seats(party, event.target.value)"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const parties = ref([
  {'name':"S&D",'seats':5,vote:2},
  {'name':"PDLS",'seats':21,vote:2},
  {'name':"Libéraux",'seats':16,vote:2},
  {'name':"PCG",'seats':2,vote:2},
  {'name':"Démocrates-Théocrates",'seats':22,vote:2},
  {'name':"Labor",'seats':6,vote:2},
  {'name':"SSP",'seats':23,vote:2},
  {'name':"INP",'seats':0,vote:2},
  {'name':"SAM",'seats':27,vote:2},
  {'name':"Républicains",'seats':9,vote:2},
  {'name':"PHNG",'seats':1,vote:2},
]);

const TOTAL_TOTAL = 132;

const for_width = ref(0);
const against_width = ref(0);
const none_width = ref(100);

const for_total = ref(0);
const against_total = ref(0);
const none_total = ref(TOTAL_TOTAL);

const total = ref(TOTAL_TOTAL);

const yes = ref("Indécision");
const color = ref("grey");

const allow_change = ref(true);

function redis_seats(party: {name: string, seats: number, vote: number}, seat: string) {
  party.seats = parseInt(seat);
  let tmp = 0;
  for(const party of parties.value) {
    console.log(party);
    tmp += party.seats;
    console.log(tmp);
  }

  console.log(tmp);

  if(tmp != TOTAL_TOTAL) {
    yes.value = "ERREUR - SIEGES MANQUANTS OU EN TROP! (" + (TOTAL_TOTAL - tmp) + " sièges manquants)";
    color.value = "#990000";
    allow_change.value = false;
  } else {
    yes.value = "...";
    color.value = "#dddddd";
    allow_change.value = true;
  }
}

function set_vote(party_name: string, vote: number) {
  if(!allow_change.value) return;

  console.log("call with", party_name, vote)
  for(const party of parties.value) {
    if(party.name != party_name) continue;

    party.vote = vote;
  }

  calculate();
}

function calculate() {
  for_total.value = 0;
  against_total.value = 0;
  none_total.value = 0;
  none_width.value = 0;

  total.value = 132;

  for(const party of parties.value) {
    switch(party.vote) {
      case 0:
        for_total.value += party.seats;
        break;
      case 1:
        against_total.value += party.seats;
        break;
      case 2:
        total.value -= party.seats;
        none_total.value += party.seats;
        break;
    }
  }

  for_width.value = for_total.value / total.value * 100;
  against_width.value = against_total.value / total.value * 100;

  if(none_total.value == TOTAL_TOTAL) {
    none_width.value = 100;
    yes.value = "Indécision";
    color.value = "grey";
    return;
  }

  if(for_total.value > total.value / 2) {
    yes.value = "Approuvé";
    color.value = "#00dd00";
  } else if(for_total.value == total.value / 2) {
    yes.value = "Indécision";
    color.value = "grey";
  } else {
    yes.value = "Rejeté";
    color.value = "#dd0000";
  }
}
</script>

<style scoped>
/** for total abs approved */
div#count span#for {
  color: #00dd00;
}

div#count span#abs {
  color: #dddd00;
}

div#count span#approved {
  color: v-bind("color");
}

div#bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  height: 10vh;
  width: 50vw;
  margin-bottom: 2rem;
  background-color: #dddd00;
}

div#bar div#for {
  position: inherit;
  width: calc(v-bind("for_width + 'vw'") / 2);
  height: inherit;
  background-color: #00dd00;
}

div#bar div#against {
  position: inherit;
  width: calc(v-bind("against_width + 'vw'") / 2);
  height: inherit;
  background-color: #dd0000;
}

div#bar div#none {
  position: inherit;
  width: calc(v-bind("none_width + 'vw'") / 2);
  height: inherit;
  background-color: #dddd00;
}

div#party-selection div {
  display: flex;
  width: 40vw;
  margin-top: 0.5rem;
}

div#party-selection div div#buttons {
  justify-content: flex-end;
}

div#party-selection div div#buttons button {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bolder;
}

div#party-selection div div#buttons button[active=false] {
  background-color: #dddddd;
}

div#party-selection div div#name {
  justify-content: flex-start;
}

div#party-selection div div#buttons button#for[active=true] {
  background-color: #00dd00;
}

div#party-selection div div#buttons button#against[active=true] {
  background-color: #dd0000;
}

div#party-selection div div#buttons button#none[active=true] {
  background-color: #dddd00;
}

div#party-selection div div#buttons button#for {
  border-radius: 8px 0px 0px 8px;
}

div#party-selection div div#buttons button#none {
  border-radius: 0px 8px 8px 0px;
}

div#party-selection div div#buttons input {
  margin-left: 1vw;
  border-radius: 8px;
  text-align: center;
}
</style>