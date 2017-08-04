use std::fs::File;
use std::io::Read;
use std::iter::Iterator;

// http://adventofcode.com/2016/day/1

/* Type Definitions */

/**
 * Enumeration & helper functions for cardinal directions.
 */
enum Direction {
	North,
	East,
	South,
	West
}

impl Direction {
	fn apply_movement(&self, amount: i32) -> (i32, i32) {
		return match *self {
			Direction::North => (0, amount),
			Direction::East => (amount, 0),
			Direction::South => (0, -amount),
			Direction::West => (-amount, 0)
		};
	}
}

fn direction_to_i32(direction: Direction) -> i32 {
	return match direction {
		Direction::North => 0,
		Direction::East => 90,
		Direction::South => 180,
		Direction::West => 270
	};
}

fn i32_to_direction(value: i32) -> Direction {
	return match value {
		0 | 360 => Direction::North,
		90 => Direction::East,
		180 => Direction::South,
		270 | -90 => Direction::West,
		_ => panic!("Illegal i32 value: {}", value)
	};
}

/**
 * Structure holding info about the current positional state on the grid.
 */
struct Position {
	direction: Direction,
	x: i32,
	y: i32
}

/**
 * From the given Direction and a "turn face" value,
 * get the new Direction. For instance, given "North"
 * and the instruction to turn right ("R"), the new
 * Direction will be "East".
 */
fn turn(from: Direction, face: char) -> Direction {
	// "face" is either "R" or "L" & affects the
	// index access of the new direction.
	// The enum contains clockwise directions,
	// so "R" will increment the index, while "L"
	// will decrement it. We also need to take care
	// of the array boundaries.
	let degree_shift = match face {
		'R' => 90,
		'L' => -90,
		_ => panic!("Unexpected facing value: {}", face)
	};

	let new_degrees = direction_to_i32(from) + degree_shift;
	return i32_to_direction(new_degrees);
}

/**
 * From the given starting point, execute the given instruction
 * and return the new destination.
 */
fn walk(position: Position, instruction: &str) -> Position {
	// Split each instruction into its direction (R/L) & length (remainder as int)
	let mut chars = instruction.chars();
	let dir = chars.nth(0).unwrap();

	let steps = chars.as_str().parse::<i32>().unwrap();
	let new_direction = turn(position.direction, dir);
	let (add_x, add_y) = new_direction.apply_movement(steps);

	return Position {
		direction: new_direction,
		x: position.x + add_x,
		y: position.y + add_y
	};
}

fn main() {
	// Read inputs & prepare content for parsing
	let mut file = File::open("assets/input.txt").unwrap();
	let mut content = String::new();
	file.read_to_string(&mut content).unwrap();

	// Cut off commas & split into list (elements e.g. "R2", "L8")
	content = content.replace(",", "");
	let tokens: Vec<&str> = content.split_whitespace().collect();

	// From the starting point, execute each instruction in sequence
	let mut position: Position = Position {
		direction: Direction::North,
		x: 0,
		y: 0
	};
	for token in tokens.iter() {
		position = walk(position, token);
	}

	// The total "distance in blocks" is calculated by adding both components
	let distance = position.x + position.y;
	println!("Final position: ({}, {})", position.x, position.y);
	println!("Distance from center: {} blocks", distance);
}
