<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Chain extends Model
{
    public $timestamps = false;

    protected $table = 'Chain';

    protected $fillable = [
        'data'
    ];
}
