<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Bookie extends Model
{
    public $timestamps = false;

    protected $table = 'Bookie';

    public function items()
    {
        return $this->hasMany('App\CategoryInfo');
    }
}
