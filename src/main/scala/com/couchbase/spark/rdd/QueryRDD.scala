/*
 * Copyright (c) 2015 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.couchbase.spark.rdd

import com.couchbase.client.java.query.{AsyncN1qlQueryRow, N1qlQuery}
import com.couchbase.spark.connection.{CouchbaseConfig, QueryAccessor}
import org.apache.spark.{Partition, SparkContext, TaskContext}
import org.apache.spark.rdd.RDD

import scala.concurrent.duration.Duration

class QueryRDD(@transient private val sc: SparkContext, query: N1qlQuery,
               bucketName: String = null,
               timeout: Option[Duration] = None)
  extends RDD[AsyncN1qlQueryRow](sc, Nil) {

  private val cbConfig = CouchbaseConfig(sc.getConf)

  override def compute(split: Partition, context: TaskContext): Iterator[AsyncN1qlQueryRow] =
    new QueryAccessor(cbConfig, Seq(query), bucketName, timeout).compute()

  override protected def getPartitions: Array[Partition] = Array(new CouchbasePartition(0))
}

object QueryRDD {

  def apply(sc: SparkContext, bucketName: String, query: N1qlQuery,
            timeout: Option[Duration] = None) =
    new QueryRDD(sc, query, bucketName, timeout)

}
