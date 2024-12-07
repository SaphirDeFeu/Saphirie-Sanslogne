<!-- eslint-disable vue/multi-word-component-names -->
<!-- eslint-disable vue/require-v-for-key -->
<!-- eslint-disable @typescript-eslint/no-unused-vars -->
<template>
  <div id="overbar">
    <!-- <div id="text">50%<br/>\/</div> -->
    <div id="superposition">
      <div id="bar">
        <div id="for"></div>
        <div id="against"></div>
      </div>    
      <div id="separator"></div>
    </div>
  </div>

  <div id="count">
    <span>
      <span id="for">{{ for_total }}</span>/
      <span id="total">{{ total }}</span> (
        <span id="abs">{{ none_total }}</span> abstentions) -> 
        <span id="approved">{{ yes }}</span> / 
        <span id="two-thirds">2/3</span>
    </span>
  </div>

  <div id="party-selection" v-for="party of parties">
    <div>
      <div id="name">{{ party.name }}</div>
      <div id="buttons">
        <button id="for" :active="party.vote == 0" @click="set_vote(party.name, 0)">Pour</button>
        <button id="against" :active="party.vote == 1" @click="set_vote(party.name, 1)">Contre</button>
        <button id="none" :active="party.vote == 2" @click="set_vote(party.name, 2)">Abstention</button>
        <input type="number" min="0" max="132" :value="party.seats" @input="event => redis_seats(party, (event.target ?? {value:0}).value)"/>
      </div>
    </div>
    <div :style="'width: inherit; height: 0; margin: 0; padding: 0; border-bottom: 4px solid ' + party.color + '; filter: brightness(90%);'"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const parties = ref([
  {name:"S&D",seats:5,vote:2,color:'#ff0000'},
  {name:"PDLS",seats:21,vote:2,color:'#ff4800'},
  {name:"Libéraux",seats:16,vote:2,color:'#ffd800'},
  {name:"PCG",seats:2,vote:2,color:'#ff00dc'},
  {name:"Démocrates-Théocrates",seats:22,vote:2,color:'#0094ff'},
  {name:"Labor",seats:6,vote:2,color:'#670000'},
  {name:"SSP",seats:23,vote:2,color:'#ff0000'},
  {name:"INP",seats:0,vote:2,color:'#00ffff'},
  {name:"SAM",seats:27,vote:2,color:'#ff7000'},
  {name:"Républicains",seats:9,vote:2,color:'#0026ff'},
  {name:"PHNG",seats:1,vote:2,color:'#b200ff'},
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
const two_thirds = ref("#dd0000");
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

  if(total.value == 0) total.value = TOTAL_TOTAL;

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
  } else {
    yes.value = "Rejeté";
    color.value = "#dd0000";
  }

  if(for_total.value >= total.value * 2 / 3) {
    two_thirds.value = "#00dd00";
  } else {
    two_thirds.value = "#dd0000";
  }
}
</script>

<style scoped>
div#overbar {  
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

div#overbar #text {
  font-size: 120%;
  text-align: center;
  margin: none;
  padding: none;
}

div#superposition {
  position: relative;
  width: 50vw;
  height: 10vh;
  margin-bottom: 2rem;
  border-radius: 8px;

}

div#superposition div#bar {
  position: absolute;
  width: inherit;
  height: inherit;
  background-color: #dddd00;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

div#superposition div#separator {
  z-index: 1;
  position: absolute;
  height: inherit;
  width: 1px;
  top: 0;
  left: calc(50vw / 2 - 1px);
  background-color: white;
}

div#count > * {
  font-size: 110%;
}

div#count > * * {
  font-size: 150%;
}

div#count span#for {
  color: #00dd00;
}

div#count span#abs {
  color: #dddd00;
}

div#count span#approved {
  color: v-bind("color");
}

div#count span#two-thirds {
  color: v-bind("two_thirds");
}

div#bar div#for {
  width: calc(v-bind("for_width + 'vw'") / 2);
  height: inherit;
  background-color: #00dd00;
  transition: width 1s ease-in-out;
}

div#bar div#against {
  width: calc(v-bind("against_width + 'vw'") / 2);
  height: inherit;
  background-color: #dd0000;
  transition: width 1s ease-in-out;
}

div#party-selection div {
  display: flex;
  width: 50vw;
  margin-top: 1vh;
}

div#party-selection div div#buttons {
  justify-content: flex-end;
}

div#party-selection div div#buttons button {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bolder;
  margin: .3vh 0;
  margin-left: 1px;
  border: none;
  background-color: #dddddd;
}

div#party-selection div div#buttons button[active=false] {
  background-color: #dddddd;
}

div#party-selection div div#name {
  justify-content: flex-start;
  font-size: 150%;
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
  margin: .3vh 0;
  margin-left: .5vw;
  border-radius: 8px;
  text-align: center;
  background-color: #f0f0f0;
  border: 1px solid white;
  font-family: Cambria, Cochin, Georgia, Times, 'Times New Roman', serif;

}

div#party-selection div div#buttons input:focus {
  outline-width: 0;
}
</style>