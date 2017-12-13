<?php

use Illuminate\Database\Seeder;

class BookieTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        $bookies = [
            'AdjaraBet',
            'BetLive',
            'CrystalBet',
            'EuropeBet',
            'LiderBet',
            'CrocoBet',
        ];


        foreach ($bookies as $bookie) {
            \Illuminate\Support\Facades\DB::table('Bookie')->insert([
                'name' => $bookie
            ]);
        }
    }
}
