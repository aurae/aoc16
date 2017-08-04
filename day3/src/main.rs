extern crate common;
extern crate permutohedron;

use std::iter::Iterator;
use common::files::read;
use permutohedron::Heap;

fn main() {
    // Read & prepare inputs
    let content = read("assets/input.txt");
    let lines: Vec<&str> = content.lines().collect();

    // Number of valid triangles, incremented by the following loop
    let mut valid_count = 0;
    
    'outer: for line in lines {
        // Generate permutations for each 3-tuple in the list of inputs
        let mut sides: Vec<i32> = line.split_whitespace()
            .map(|item| item.parse::<i32>().unwrap())
            .collect();

        let heap = Heap::new(&mut sides);

        // Check the integrity of each line by comparing the sum of
        // each permutation's sum of the first two elements
        // against the remaining third one.
        for permutation in heap {
            if permutation[0] + permutation[1] <= permutation[2] {
                // Invalid triangle:
                // Don't count this one & immediately continue
                // with the next line
                continue 'outer;
            }
        }

        // Valid triangle found
        valid_count += 1;
    }

    println!("Number of valid triangles: {}", valid_count);
}
