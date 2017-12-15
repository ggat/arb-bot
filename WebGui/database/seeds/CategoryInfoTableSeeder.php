<?php

use Illuminate\Database\Seeder;
use \Illuminate\Support\Facades\DB;

class CategoryInfoTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {

        $bookies = \App\Bookie::all();

        $categories = [
            'Georgia' => [
                'National league',
                'East league',
                'West league'
            ],
            'Italy' => [
                'Serie A',
                'Coppa Italia'
            ],
            'France' => [
                'Ligue 1',
                'Ligue 2',
                'National',
                'Division 1'
            ]
        ];

        foreach ($bookies as $bookie) {
            foreach ($categories as $categoryName => $subCategories) {

                $category = App\CategoryInfo::create([
                    'name' => $categoryName . '_' . $bookie->name,
                    'bookie_id' => $bookie->id
                ]);

                foreach ($subCategories as $subCategory) {
                    DB::table('CategoryInfo')->insert([
                        'name' => $subCategory . '_' . $bookie->name,
                        'bookie_id' => $bookie->id,
                        'category_info_id' => $category->id
                    ]);
                }
            }
        }
    }
}
