// Parameters
// They must follow the following format:
// NUMBER = [ TIME, MODE ]
const CURR = [6732, 1];
const __N1 = [5768, 0];
const __N2 = [2971, 0];
const __N3 = [2091, 0];
const __N4 = [2270, 0];
const __N5 = [5733, 2];

const current_day = 1750;
// Current time must follow the format [hour, minutes, seconds]
const current_time = 16523;

function update_time(obj, timing) {
  let __t1 = obj.end.time + timing;
  console.log("t", __t1);
  while (__t1 >= 24000) {
    __t1 -= 24000;
    obj.end.date++;
  }

  obj.end.time = __t1;
  return obj;
}

let __O1 = {
  start: {
    date: current_day,
    time: current_time,
  },
  end: {
    date: current_day,
    time: current_time,
  },
  mode: CURR[1],
};
__O1 = update_time(__O1, CURR[0]);

let __O2 = {
  start: {
    date: __O1.end.date,
    time: __O1.end.time,
  },
  end: {
    date: __O1.end.date,
    time: __O1.end.time,
  },
  mode: __N1[1],
};
__O2 = update_time(__O2, __N1[0]);

let __O3 = {
  start: {
    date: __O2.end.date,
    time: __O2.end.time,
  },
  end: {
    date: __O2.end.date,
    time: __O2.end.time,
  },
  mode: __N2[1],
};
__O3 = update_time(__O3, __N2[0]);

let __O4 = {
  start: {
    date: __O3.end.date,
    time: __O3.end.time,
  },
  end: {
    date: __O3.end.date,
    time: __O3.end.time,
  },
  mode: __N3[1],
};
__O4 = update_time(__O4, __N3[0]);

let __O5 = {
  start: {
    date: __O4.end.date,
    time: __O4.end.time,
  },
  end: {
    date: __O4.end.date,
    time: __O4.end.time,
  },
  mode: __N4[1],
};
__O5 = update_time(__O5, __N4[0]);

let __O6 = {
  start: {
    date: __O5.end.date,
    time: __O5.end.time,
  },
  end: {
    date: __O5.end.date,
    time: __O5.end.time,
  },
  mode: __N5[1],
};
__O6 = update_time(__O6, __N5[0]);

console.log(__O1);
console.log(__O2);
console.log(__O3);
console.log(__O4);
console.log(__O5);
console.log(__O6);
