#[macro_use]
extern crate maplit;
extern crate num;
extern crate common;

use std::collections::HashMap;
use std::iter::Iterator;
use num::clamp;
use common::files::read;

const KEYPAD: [[i8; 3]; 3] = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9]
];

fn main() {
    // Read & prepare inputs:
    // Each line contains a set of instructions, after which a button on the Keypad is pressed.
    let content: String = read("assets/input.txt");
    let lines : Vec<&str> = content.lines().collect();

    // Declare the movement constants
    let directions: HashMap<char, (i8, i8)> = hashmap! {
        'U' => (0, -1),
        'R' => (1, 0),
        'D' => (0, 1),
        'L' => (-1, 0)
    };

    // Starting at the Keypad's "5" button (array coords "1, 1"),
    // execute each line's instructions and collect the inputs.
    let rows = KEYPAD.len() as i8;
    let cols = KEYPAD[0].len() as i8;
    let mut position = (1, 1);

    let mut door_digits: Vec<i8> = vec![];
    for line in lines {
        for char in line.chars() {
            let result = directions.get(&char);
            match result {
                Some(p) => {
                    position.0 = clamp(position.0 + p.0, 0, cols - 1);
                    position.1 = clamp(position.1 + p.1, 0, rows - 1);
                },
                _ => panic!("Unknown directional char")
            };
        }

        door_digits.push(KEYPAD[position.1 as usize][position.0 as usize]);
    }

    let door_code = door_digits.iter()
        .map(|i| i.to_string())
        .collect::<String>();
    println!("Door Code: {}", door_code);
}
