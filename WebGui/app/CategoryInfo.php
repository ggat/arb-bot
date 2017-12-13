<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class CategoryInfo extends Model
{
    protected $table = 'CategoryInfo';
    public $timestamps = false;

    protected $with = [
        'items'
    ];

    public function items()
    {
        return $this->hasMany('App\CategoryInfo');
    }
}
