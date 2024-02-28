fn main() {
  let args = std::env::args().collect::<Vec<String>>();
  if args[1].as_str() == "t" {
    from_tick(args);
  } else if args[1].as_str() == "T" {
    from_time(args);
  }
}

fn from_tick(args: Vec<String>) {
  let ticks = match args[2].parse::<u16>() {
    Ok(v) => v,
    Err(e) => {
      eprintln!("Error {}", e);
      return;
    }
  };

  let tick_hour = (ticks as f32 / 1000.0).floor() as u16;
  let hour = (6 + tick_hour) % 24;
  
  let tick_minute = ticks % 1000;
  let minutes_with_sec = tick_minute as f32 / (16.0 + 2.0 / 3.0);

  let sec = (minutes_with_sec % 1.0) * 60.0;

  println!("{}:{}:{}", hour, minutes_with_sec.floor() as u16, sec.floor() as u16);
}

fn from_time(args: Vec<String>) {
  let timings = args[2].split(":").collect::<Vec<&str>>();
  let hour = match timings[0].parse::<u16>() {
    Ok(v) => v,
    Err(e) => {
      eprintln!("Error {}", e);
      return;
    }
  };

  let minutes = match timings[1].parse::<u16>() {
    Ok(v) => v,
    Err(e) => {
      eprintln!("Error {}", e);
      return;
    }
  };

  let seconds = match timings[2].parse::<u16>() {
    Ok(v) => v,
    Err(e) => {
      eprintln!("Error {}", e);
      return;
    }
  };

  let mut results = 0;
  results += (hour * 1000 + 18000) % 24000;
  
  let mut minutes_with_sec: u16 = ((seconds as f64 / 60.0) * (16.0 + 2.0 / 3.0)).floor() as u16;
  minutes_with_sec += (minutes as f64 * (1000.0 / 60.0)).floor() as u16;
  
  results += minutes_with_sec;
  println!("{}", results);
}